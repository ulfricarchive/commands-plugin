package com.ulfric.plugin.commands;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ulfric.broken.ErrorHandler;
import com.ulfric.broken.StandardCriteria;
import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.application.Container;
import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.extension.postconstruct.PostConstruct;
import com.ulfric.i18n.content.Details;
import com.ulfric.plugin.commands.argument.MissingArgumentException;
import com.ulfric.plugin.commands.argument.defaults.DefaultResolversContainer;
import com.ulfric.plugin.commands.confirmation.ConfirmationRequiredException;
import com.ulfric.plugin.commands.confirmation.ConfirmationStage;
import com.ulfric.plugin.commands.exception.CommandChannel;
import com.ulfric.plugin.commands.exception.CommandException;
import com.ulfric.plugin.commands.exception.MustBePlayerException;
import com.ulfric.plugin.commands.function.defaults.DefaultFunctionsContainer;
import com.ulfric.plugin.commands.internal.Invoker;
import com.ulfric.plugin.commands.internal.Registry;
import com.ulfric.plugin.commands.internal.dragoon.CommandFeature;
import com.ulfric.plugin.commands.internal.dragoon.ResolverFeature;
import com.ulfric.plugin.commands.permissions.MissingPermissionException;
import com.ulfric.plugin.commands.permissions.PermissionVerificationStage;
import com.ulfric.plugin.locale.TellService;

public class CommandsContainer extends Container {

	@Inject
	private ObjectFactory factory;

	@Inject
	@CommandChannel
	private ErrorHandler errorHandler;

	@Inject(optional = true)
	private Logger logger;

	public CommandsContainer() {
		install(ResolverFeature.class);
		install(CommandFeature.class);
		install(DefaultResolversContainer.class);
		install(PermissionVerificationStage.class);
		install(ConfirmationStage.class);
		install(DefaultFunctionsContainer.class);

		addBootHook(this::bindInvoker);
		addShutdownHook(this::unbindInvoker);
		addShutdownHook(this::unbindRegistry);
	}

	@PostConstruct
	private void setupErrorHandler() {
		errorHandler.withHandler(MissingPermissionException.class)
			.setCriteria(StandardCriteria.EXACT_TYPE_MATCH)
			.setAction(permissionCheck ->
				TellService.sendMessage(permissionCheck.getContext().getSender(), permissionCheck.getPermissionMessage(),
					Details.of("node", permissionCheck.getPermissionNode())))
			.add();

		errorHandler.withHandler(MissingArgumentException.class)
			.setCriteria(StandardCriteria.EXACT_TYPE_MATCH)
			.setAction(requiredArgument ->
				TellService.sendMessage(requiredArgument.getContext().getSender(), requiredArgument.getDefinition().getMessage(),
					Details.of("argument", requiredArgument.getDefinition())))
			.add();
	
		errorHandler.withHandler(MustBePlayerException.class)
			.setCriteria(StandardCriteria.EXACT_TYPE_MATCH)
			.setAction(mustBePlayer ->
				TellService.sendMessage(mustBePlayer.getContext().getSender(), "command-must-be-player"))
			.add();

		errorHandler.withHandler(ConfirmationRequiredException.class)
			.setCriteria(StandardCriteria.EXACT_TYPE_MATCH)
			.setAction(confirmation ->
				TellService.sendMessage(confirmation.getContext().getSender(), confirmation.getConfirmationMessage()))
			.add();
	
		errorHandler.withHandler(CommandException.class)
			.setCriteria(StandardCriteria.INSTANCE_OF)
			.skipIfHandled()
			.setAction(exit -> TellService.sendMessage(exit.getContext().getSender(), "command-exception"))
			.add();
	
		errorHandler.withHandler(Exception.class)
			.setCriteria(StandardCriteria.INSTANCE_OF)
			.skipIfHandled()
			.setAction(exception -> {
				// TODO notify the player
				if (logger != null) {
					logger.log(Level.SEVERE, "Command failed execution", exception);
				}
			})
			.add();
	}

	private void bindInvoker() {
		Map<Class<? extends Command>, Invoker> cache = new IdentityHashMap<>();
		factory.bind(Invoker.class).toFunction(parameters -> {
			@SuppressWarnings("unchecked")
			Class<? extends Command> command = (Class<? extends Command>) parameters.getArguments()[0];
			return cache.computeIfAbsent(command, Invoker::new);
		});
	}

	private void unbindInvoker() {
		factory.bind(Invoker.class).toNothing();
	}

	private void unbindRegistry() {
		factory.bind(Registry.class).toNothing();
	}

}
