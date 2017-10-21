package com.ulfric.plugin.commands;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.command.CommandSender;

import com.ulfric.commons.bukkit.command.CommandSenderHelper;
import com.ulfric.commons.naming.Name;
import com.ulfric.dragoon.extension.intercept.asynchronous.Asynchronous;
import com.ulfric.dragoon.reflect.Classes;
import com.ulfric.dragoon.reflect.Instances;
import com.ulfric.dragoon.stereotype.Stereotypes;
import com.ulfric.plugin.commands.argument.Argument;
import com.ulfric.plugin.commands.argument.ArgumentDefinition;
import com.ulfric.plugin.commands.argument.Arguments;
import com.ulfric.plugin.commands.argument.MissingArgumentException;
import com.ulfric.plugin.commands.argument.ResolutionRequest;
import com.ulfric.plugin.commands.argument.Resolver;
import com.ulfric.plugin.commands.confirmation.Confirmation;
import com.ulfric.plugin.commands.confirmation.ConfirmationNotRequired;
import com.ulfric.plugin.commands.confirmation.ConfirmationRequiredException;
import com.ulfric.plugin.commands.confirmation.ExpiringConfirmation;
import com.ulfric.plugin.commands.confirmation.RequireConfirmation;
import com.ulfric.plugin.restrictions.RestrictedActionService;
import com.ulfric.plugin.restrictions.RestrictedContext;
import com.ulfric.tryto.TryTo;

public final class Invoker {

	private static final Map<Class<? extends Command>, Invoker> INVOKERS = new IdentityHashMap<>();
	public static final String INTERNAL_ERROR_MESSAGE = "command-internal-error";

	public static Invoker of(Class<? extends Command> command) {
		Objects.requireNonNull(command, "command");

		if (command == Command.class) {
			throw new IllegalArgumentException("Cannot create an invoker of " + command);
		}

		if (Modifier.isAbstract(command.getModifiers())) { // TODO static utility
			throw new IllegalArgumentException("Command must not be abstract");
		}

		return INVOKERS.computeIfAbsent(command, Invoker::new);
	}

	private final Class<? extends Command> command;
	private final Invoker superCommand;
	private final List<Permission> permissions;
	private final List<ArgumentDefinition> arguments;
	private final Map<String, Invoker> subcommands = new CaseInsensitiveMap<>();
	private final String restrictionContext;
	private final Asynchronous asynchronous;
	private final RequireConfirmation confirmationContext;
	private final Confirmation confirmation;

	private Invoker(Class<? extends Command> command) {
		this.command = command;
		this.superCommand = superCommand();
		this.arguments = createArgumentDefinitions();
		this.permissions = createPermissions();
		this.restrictionContext = restrictionContext();
		this.asynchronous = asynchronousContext();
		this.confirmationContext = confirmationContext();
		this.confirmation = generateConfirmationFromConfirmationContext();
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
			Argument argument = Stereotypes.getFirst(field, Argument.class);
			if (argument == null) {
				continue;
			}
			field.setAccessible(true);

			ArgumentDefinition definition = new ArgumentDefinition();
			definition.setField(field);
			definition.setMessage(argument.message());
			definition.setOptional(argument.optional());
			definition.setName(field.getName());
			definition.setType(field.getGenericType());
			arguments.add(definition);
		}

		return arguments;
	}

	private List<Permission> createPermissions() { // TODO support repeatable
		return Stereotypes.getAll(command, Permission.class);
	}

	private String restrictionContext() {
		Restricted restricted = Stereotypes.getFirst(command, Restricted.class);

		if (restricted == null) {
			return null;
		}

		return restricted.value().isEmpty() ? null : restricted.value();
	}

	private Asynchronous asynchronousContext() {
		return Stereotypes.getFirst(command, Asynchronous.class);
	}

	private RequireConfirmation confirmationContext() {
		return Stereotypes.getFirst(command, RequireConfirmation.class);
	}

	private Confirmation generateConfirmationFromConfirmationContext() {
		if (confirmationContext == null) {
			return ConfirmationNotRequired.INSTANCE;
		}

		return new ExpiringConfirmation(confirmationContext);
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
		Name name = Stereotypes.getFirst(command, Name.class);
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
		List<Alias> aliases = Stereotypes.getAll(command, Alias.class);
		return aliases.stream()
				.map(Alias::value)
				.flatMap(Arrays::stream)
				.collect(Collectors.toList());
	}

	public String getUsage() {
		Usage usage = Stereotypes.getFirst(command, Usage.class);
		return usage == null ? "/" + getName() : usage.value();
	}

	public String getDescription() {
		Description description = Stereotypes.getFirst(command, Description.class);
		return description == null ? "" : description.value();
	}

	public Asynchronous getAsynchronous() {
		return asynchronous;
	}

	public boolean isRoot() {
		return superCommand == null;
	}

	public void run(Context context) {
		UUID uniqueId = CommandSenderHelper.getUniqueId(context.getSender());
		if (uniqueId != null) {
			if (!confirmation.test(uniqueId)) {
				throw new ConfirmationRequiredException(confirmationContext.message());
			}
		}

		if (restrictionContext != null) {
			RestrictedContext restriction = new RestrictedContext();
			restriction.setAction(restrictionContext);
			restriction.setSender(context.getSender());

			RestrictedActionService.doRestricted(() -> runUnrestricted(context), restriction);
			return;
		}

		runUnrestricted(context);
	}

	private void runUnrestricted(Context context) {
		Command command = Instances.instance(this.command);
		command.context = context;
		context.setCommand(command);

		prerun(context);

		command.run();
	}

	private void prerun(Context context) {
		if (!isRoot()) {
			superCommand.prerun(context);
		}

		runPermissionsChecks(context);
		setupArguments(context);
	}

	private void runPermissionsChecks(Context context) {
		CommandSender sender = context.getSender();
		for (Permission permission : permissions) {
			if (sender.hasPermission(permission.value())) {
				continue;
			}

			throw new MissingPermissionException(permission.value(), permission.message());
		}
	}

	private void setupArguments(Context context) {
		for (ArgumentDefinition definition : arguments) {
			ResolutionRequest request = new ResolutionRequest();
			request.setContext(context);
			request.setDefinition(definition);
			request.setCommand(command);
			setupArgument(request);
		}
	}

	private void setupArgument(ResolutionRequest request) { // TODO cleanup method
		Arguments commandArguments = request.getContext().getArguments();
		if (commandArguments.getArguments() == null) {
			missingArgument(request.getDefinition());
			return;
		}
		List<String> enteredArguments = commandArguments.getArguments().get(command);
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
			TryTo.run(() -> {
				FieldUtils.writeField(request.getDefinition().getField(),
						request.getContext().getCommand(), resolved); });
			return;
		}

		missingArgument(request.getDefinition());
	}

	private void missingArgument(ArgumentDefinition definition) {
		if (!definition.getOptional()) {
			throw new MissingArgumentException(definition.getName(), definition.getMessage());
		}
	}

}
