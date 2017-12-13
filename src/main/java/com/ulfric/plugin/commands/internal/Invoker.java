package com.ulfric.plugin.commands.internal;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

import com.ulfric.broken.ErrorHandler;
import com.ulfric.commons.collection.Collectors2;
import com.ulfric.commons.concurrent.FutureHelper;
import com.ulfric.commons.naming.Name;
import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.extension.intercept.asynchronous.Asynchronous;
import com.ulfric.dragoon.extension.intercept.asynchronous.AsynchronousInterceptor;
import com.ulfric.dragoon.extension.postconstruct.PostConstruct;
import com.ulfric.dragoon.reflect.Classes;
import com.ulfric.dragoon.stereotype.Stereotypes;
import com.ulfric.plugin.commands.Alias;
import com.ulfric.plugin.commands.Command;
import com.ulfric.plugin.commands.CommandPreRunEvent;
import com.ulfric.plugin.commands.Context;
import com.ulfric.plugin.commands.argument.Argument;
import com.ulfric.plugin.commands.argument.ArgumentDefinition;
import com.ulfric.plugin.commands.argument.Arguments;
import com.ulfric.plugin.commands.argument.EnteredSyntax;
import com.ulfric.plugin.commands.argument.MissingArgumentException;
import com.ulfric.plugin.commands.argument.ResolutionRequest;
import com.ulfric.plugin.commands.argument.Resolver;
import com.ulfric.plugin.commands.exception.CommandChannel;
import com.ulfric.plugin.commands.exception.CommandException;
import com.ulfric.plugin.tasks.executor.EnsureMainThreadExecutorSupplier;
import com.ulfric.tryto.TryTo;

public class Invoker implements CommandExecutor {

	private final Class<? extends Command> command;
	private final String name;
	private final List<String> aliases;
	private final Map<String, Invoker> children = new CaseInsensitiveMap<>();

	@Inject
	private ObjectFactory factory;

	@Inject
	@CommandChannel
	private ErrorHandler handler;

	@Inject
	private PluginManager pluginManager;

	private Executor executor;
	private Invoker parent;
	private Invoker inherits;
	private List<ArgumentDefinition> arguments;

	public Invoker(Class<? extends Command> command) {
		Objects.requireNonNull(command, "command");

		this.command = command;
		this.name = name();
		this.aliases = aliases();
	}

	@PostConstruct
	private void setup() {
		parent = parent();
		executor = executor(command);
		parent = parent();
		inherits = inherits();
		arguments = arguments();
	}

	private Executor executor(AnnotatedElement element) {
		Asynchronous asynchronous = Stereotypes.getFirst(element, Asynchronous.class);
		Class<? extends Supplier<? extends Executor>> executorType
				= asynchronous == null ? EnsureMainThreadExecutorSupplier.class : asynchronous.value();
		return AsynchronousInterceptor.executor(factory, executorType);
	}

	private Executor executorOrDefault(AnnotatedElement element) {
		Asynchronous asynchronous = Stereotypes.getFirst(element, Asynchronous.class);
		if (asynchronous == null) {
			return executor;
		}
		return AsynchronousInterceptor.executor(factory, asynchronous.value());
	}

	private Invoker inherits() {
		Class<?> superCommand = command.getSuperclass();
		if (Command.class == superCommand || !Command.class.isAssignableFrom(superCommand)) {
			return null;
		}

		return factory.request(Invoker.class, superCommand.asSubclass(Command.class));
	}

	private Invoker parent() {
		Class<?> superCommand = Classes.getNonDynamic(command.getSuperclass());
		return firstRunnableInvoker(superCommand);
	}

	private Invoker firstRunnableInvoker(Class<?> superCommand) {
		if (!Command.class.isAssignableFrom(superCommand)) {
			return null;
		}
		if (Modifier.isAbstract(superCommand.getModifiers())) { // TODO static utility
			return firstRunnableInvoker(superCommand.getSuperclass());
		}
		return factory.request(Invoker.class, superCommand.asSubclass(Command.class));
	}

	private String name() {
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

	private List<String> aliases() {
		List<Alias> aliases = Stereotypes.getAll(command, Alias.class);
		return aliases.stream()
				.map(Alias::value)
				.flatMap(Arrays::stream)
				.distinct()
				.collect(Collectors2.toUnmodifiableList());
	}

	private List<ArgumentDefinition> arguments() {
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
			definition.setExecutor(executorOrDefault(field));
			arguments.add(definition);
		}

		return arguments;
	}

	public boolean registerWithParent() {
		if (parent != null) {
			registerWithParent(name);
			aliases.forEach(this::registerWithParent);
			return true;
		}
		return false;
	}

	private void registerWithParent(String name) {
		parent.children.put(name, this);
	}

	public boolean unregisterWithParent() {
		if (parent != null) {
			unregisterWithParent(name);
			aliases.forEach(this::unregisterWithParent);
			return true;
		}

		return false;
	}

	private void unregisterWithParent(String name) {
		parent.children.remove(name, this);
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		Context context = new Context();

		context.setSender(sender);
		context.setArguments(resolveCommandToArgumentHierarchyPositions(label, args));
		context.setExecutionId(UUID.randomUUID());

		Invoker invoker = factory.request(Invoker.class, context.getArguments().getArguments().lastKey());

		invoker.handle(context)
			.exceptionally(handler.asFutureHandler());

		return true;
	}

	private Arguments resolveCommandToArgumentHierarchyPositions(String label, String[] args) {
		Arguments arguments = new Arguments();
		arguments.setEntered(Arrays.stream(args).collect(Collectors2.toUnmodifiableList()));

		OrderedMap<Class<? extends Command>, EnteredSyntax> argumentsByCommand = new LinkedMap<>();
		EnteredSyntax entered = new EnteredSyntax();
		entered.setLabel(label);
		entered.setArguments(new ArrayList<>(arguments.getEntered()));
		argumentsByCommand.put(command, entered);
		arguments.setArguments(argumentsByCommand);

		resolveCommandToArgumentHierarchyPositions(arguments);
		return arguments;
	}

	private void resolveCommandToArgumentHierarchyPositions(Arguments arguments) { // TODO cleanup
		Map<Class<? extends Command>, EnteredSyntax> argumentsByCommand = arguments.getArguments();

		EnteredSyntax entered = argumentsByCommand.get(command);
		if (entered == null) {
			return;
		}

		List<String> enteredArguments = entered.getArguments();
		if (CollectionUtils.isEmpty(enteredArguments)) {
			return;
		}

		for (int x = 0, l = enteredArguments.size(); x < l; x++) {
			String argument = enteredArguments.get(x);
			Invoker child = getChild(argument);
			if (child != null) {
				EnteredSyntax childEntered = new EnteredSyntax();
				childEntered.setLabel(argument);

				entered.setArguments(new ArrayList<>(enteredArguments.subList(0, x)));
				childEntered.setArguments(new ArrayList<>(enteredArguments.subList(x + 1, l)));
				argumentsByCommand.put(child.command, childEntered);
				child.resolveCommandToArgumentHierarchyPositions(arguments);
				return;
			}
		}
	}

	private Invoker getChild(String name) {
		return children.get(name);
	}

	private CompletableFuture<Void> handle(Context context) {
		Command run = factory.request(command);

		run.setContext(context);
		context.setCommand(run);

		CommandException failure = callEvents(context);
		if (failure != null) {
			return FutureHelper.exceptionally(failure);
		}

		return setupArguments(context)
			.thenRunAsync(run::prerun, executor)
			.thenRun(run)
			.thenRun(run::postrun);
	}

	private CommandException callEvents(Context context) {
		if (inherits != null) {
			CommandException exception = inherits.callEvents(context);
			if (exception != null) {
				return exception;
			}
		}

		CommandPreRunEvent event = new CommandPreRunEvent(context, command);
		pluginManager.callEvent(event);
		return event.getFailure();
	}

	private CompletableFuture<Void> setupArguments(Context context) {
		CompletableFuture<Void> future;
		if (inherits != null) {
			future = inherits.setupArguments(context);
		} else {
			future = FutureHelper.empty();
		}

		for (ArgumentDefinition definition : arguments) {
			ResolutionRequest request = new ResolutionRequest();
			request.setContext(context);
			request.setDefinition(definition);
			request.setCommand(command);
			future = future.thenRunAsync(() -> setupArgument(request), definition.getExecutor());
		}

		return future;
	}

	private void setupArgument(ResolutionRequest request) { // TODO cleanup method
		Arguments commandArguments = request.getContext().getArguments();
		if (commandArguments.getArguments() == null) {
			missingArgument(request);
			return;
		}

		EnteredSyntax entered = commandArguments.getArguments().get(command);
		if (entered == null) {
			missingArgument(request);
			return;
		}

		List<String> enteredArguments = entered.getArguments();
		if (enteredArguments == null) {
			missingArgument(request);
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
			TryTo.run(() -> FieldUtils.writeField(request.getDefinition().getField(),
						request.getContext().getCommand(), resolved));
			return;
		}

		missingArgument(request);
	}

	private void missingArgument(ResolutionRequest request) {
		ArgumentDefinition definition = request.getDefinition();
		if (!definition.getOptional()) {
			Context context = request.getContext();
			throw new MissingArgumentException(context, definition);
		}
	}

	protected Class<? extends Command> getCommand() {
		return command;
	}

	protected String getName() {
		return name;
	}

	protected List<String> getAliases() {
		return aliases;
	}

}
