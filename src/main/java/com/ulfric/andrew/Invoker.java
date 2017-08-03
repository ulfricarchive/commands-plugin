package com.ulfric.andrew;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.ulfric.andrew.argument.Argument;
import com.ulfric.andrew.argument.ArgumentDefinition;
import com.ulfric.andrew.argument.MissingArgumentException;
import com.ulfric.andrew.argument.ResolutionRequest;
import com.ulfric.andrew.argument.Resolver;
import com.ulfric.commons.naming.Name;
import com.ulfric.dragoon.reflect.Classes;
import com.ulfric.dragoon.reflect.Instances;
import com.ulfric.tryto.Try;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Invoker implements Command {

	private static final Map<Class<? extends Command>, Invoker> INVOKERS = new IdentityHashMap<>();
	public static final String INTERNAL_ERROR_MESSAGE = "command-internal-error";

	public static Invoker of(Class<? extends Command> command) {
		Objects.requireNonNull(command, "command");

		if (command == Invoker.class) {
			throw new IllegalArgumentException("Cannot create an invoker of invoker");
		}

		if (Modifier.isAbstract(command.getModifiers())) { // TODO static utility
			throw new IllegalArgumentException("Command must not be abstract");
		}

		return INVOKERS.computeIfAbsent(command, Invoker::new);
	}

	private final Class<? extends Command> command;
	private final Invoker superCommand;
	private final List<String> permissions;
	private final List<ArgumentDefinition> arguments;
	private final Map<String, Invoker> subcommands = new CaseInsensitiveMap<>();

	private Invoker(Class<? extends Command> command) {
		this.command = command;
		this.superCommand = superCommand();
		this.arguments = createArgumentDefinitions();
		this.permissions = createPermissions();
	}

	private Invoker superCommand() {
		Class<?> superClass = Classes.getNonDynamic(command.getSuperclass());
		return superCommand(superClass);
	}

	private Invoker superCommand(Class<?> superClass) {
		if (!Command.class.isAssignableFrom(superClass)) {
			return null;
		}
		if (Modifier.isAbstract(superClass.getModifiers())) { // TODO static utility
			return superCommand(superClass.getSuperclass());
		}
		return Invoker.of(superClass.asSubclass(Command.class));
	}

	private List<ArgumentDefinition> createArgumentDefinitions() {
		List<ArgumentDefinition> arguments = new ArrayList<>();

		for (Field field : command.getDeclaredFields()) {
			Argument argument = field.getAnnotation(Argument.class); // TODO support stereotype
			if (argument == null) {
				continue;
			}
			field.setAccessible(true);

			ArgumentDefinition definition = new ArgumentDefinition();
			definition.setField(field);
			definition.setOptional(argument.optional());
			definition.setName(field.getName());
			definition.setType(field.getGenericType());
			arguments.add(definition);
		}

		return arguments;
	}

	private List<String> createPermissions() { // TODO support repeatable
		Permission permission = command.getAnnotation(Permission.class); // TODO support stereotype
		return permission == null ? Collections.emptyList() : Collections.singletonList(permission.value());
	}

	public void registerWithParent() {
		if (!isRoot()) {
			registerWithParent(getName());
			getAliases().forEach(this::registerWithParent);
		}
	}

	private void registerWithParent(String name) {
		superCommand.subcommands.put(name, this);
	}

	public Class<? extends Command> getCommand() {
		return command;
	}

	public void unregisterWithParent() {
		if (!isRoot()) {
			unregisterWithParent(getName());
			getAliases().forEach(this::unregisterWithParent);
		}
	}

	private void unregisterWithParent(String name) {
		superCommand.subcommands.remove(name, this);
	}

	public Invoker getChild(String name) {
		return subcommands.get(name);
	}

	public String getName() {
		Name name = command.getAnnotation(Name.class); // TODO stereotype
		if (name == null) {
			return inferName();
		}
		return name.value();
	}

	private String inferName() {
		String name = command.getSimpleName();
		if (name.endsWith("Command")) {
			name = name.substring(0, name.length() - "Command".length());
		}
		return name.toLowerCase();
	}

	public List<String> getAliases() {
		Alias aliases = command.getAnnotation(Alias.class); // TODO stereotype
		if (aliases == null) {
			return Collections.emptyList();
		}
		return Arrays.asList(aliases.value());
	}

	public String getUsage() {
		Usage usage = command.getAnnotation(Usage.class); // TODO stereotype
		return usage == null ? "/" + getName() : usage.value();
	}

	public String getDescription() {
		Description description = command.getAnnotation(Description.class); // TODO stereotype
		return description == null ? "" : description.value();
	}

	public boolean shouldRunOnMainThread() {
		return command.isAnnotationPresent(Sync.class); // TODO stereotype
	}

	public boolean isRoot() {
		return superCommand == null;
	}

	@Override
	public void run(Context context) {
		Command command = Instances.instance(this.command);
		context.setCommand(command);

		if (!isRoot()) {
			superCommand.prerun(context);
		}

		prerun(context);
		command.run(context);
	}

	private void prerun(Context context) {
		runPermissionsChecks(context);
		setupArguments(context);
	}

	private void runPermissionsChecks(Context context) {
		Sender sender = context.getSender();
		for (String node : permissions) {
			if (sender.hasPermission(node)) {
				continue;
			}

			throw new MissingPermissionException(node);
		}
	}

	private void setupArguments(Context context) {
		for (ArgumentDefinition definition : arguments) {
			ResolutionRequest request = new ResolutionRequest();
			request.setContext(context);
			request.setDefinition(definition);
			setupArgument(request);
		}
	}

	private void setupArgument(ResolutionRequest request) {
		List<String> enteredArguments = request.getContext().getArguments().get(command);
		if (enteredArguments == null) {
			missingArgument(request.getDefinition());
			return;
		}

		Iterator<String> arguments = enteredArguments.iterator();

		while (arguments.hasNext()) {
			String argument = arguments.next();
			request.setArgument(argument);
			Object resolved = Resolver.resolve(request);
			if (resolved == null) {
				continue;
			}
			arguments.remove();
			Try.toRun(() ->
				FieldUtils.writeField(request.getDefinition().getField(),
						request.getContext().getCommand(), resolved));
			return;
		}

		missingArgument(request.getDefinition());
	}

	private void missingArgument(ArgumentDefinition definition) {
		if (!definition.getOptional()) {
			throw new MissingArgumentException(definition.getName());
		}
	}

}
