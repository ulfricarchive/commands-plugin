package com.ulfric.plugin.commands.internal;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import com.ulfric.broken.ErrorHandler;
import com.ulfric.broken.StandardCriteria;
import com.ulfric.commons.time.TemporalHelper;
import com.ulfric.i18n.content.Details;
import com.ulfric.plugin.commands.CommandException;
import com.ulfric.plugin.commands.Context;
import com.ulfric.plugin.commands.Invoker;
import com.ulfric.plugin.commands.Labels;
import com.ulfric.plugin.commands.MissingPermissionException;
import com.ulfric.plugin.commands.MustBePlayerException;
import com.ulfric.plugin.commands.argument.Arguments;
import com.ulfric.plugin.commands.argument.MissingArgumentException;
import com.ulfric.plugin.locale.TellService;

final class Dispatcher extends org.bukkit.command.Command {

	final Invoker command;
	final Runner runner;
	private final Logger logger;
	private final ErrorHandler errorHandler;

	Dispatcher(Runner runner, Invoker command, Logger logger, ErrorHandler errorHandler) {
		super(command.getName(), command.getDescription(), command.getUsage(), command.getAliases());
		this.runner = runner;
		this.command = command;
		this.logger = logger;
		this.errorHandler = errorHandler;

		setupErrorHandler();
	}

	private void setupErrorHandler() { // TODO better way for doing this
		errorHandler.withHandler(MissingPermissionException.class)
			.setCriteria(StandardCriteria.EXACT_TYPE_MATCH)
			.setAction(permissionCheck ->
				TellService.sendMessage(permissionCheck.getContext().getSender(), "command-no-permission",
					Details.of("node", permissionCheck.getMessage())))
			.add();

		errorHandler.withHandler(MissingArgumentException.class)
			.setCriteria(StandardCriteria.EXACT_TYPE_MATCH)
			.setAction(requiredArgument ->
				TellService.sendMessage(requiredArgument.getContext().getSender(), "command-missing-argument",
					Details.of("argument", requiredArgument.getMessage())))
			.add();

		errorHandler.withHandler(MustBePlayerException.class)
			.setCriteria(StandardCriteria.EXACT_TYPE_MATCH)
			.setAction(mustBePlayer ->
				TellService.sendMessage(mustBePlayer.getContext().getSender(), "command-must-be-player",
					Details.of("sender", mustBePlayer.getMessage())))
			.add();

		errorHandler.withHandler(CommandException.class)
			.setCriteria(StandardCriteria.INSTANCE_OF)
			.skipIfHandled()
			.setAction(exit -> TellService.sendMessage(exit.getContext().getSender(), exit.getMessage()))
			.add();

		errorHandler.withHandler(Exception.class)
			.setCriteria(StandardCriteria.INSTANCE_OF)
			.skipIfHandled()
			.setAction(exception -> logger.log(Level.SEVERE, "Command failed execution", exception))
			.add();
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] arguments) {
		Context context = createContext(sender, label, arguments);

		run(context);

		return true;
	}

	private Context createContext(CommandSender sender, String label, String[] arguments) {
		Context context = new Context();
		context.setLogger(logger);
		context.setCreation(TemporalHelper.instantNow());
		context.setSender(sender);
		context.setCommandLine(recreateCommandLine(label, arguments));
		addArguments(context, arguments);
		addLabel(context, label);
		return context;
	}

	private String recreateCommandLine(String label, String[] arguments) {
		StringJoiner joiner = new StringJoiner(" ");
		joiner.add(label);
		for (String argument : arguments) {
			joiner.add(argument);
		}
		return joiner.toString();
	}

	private void addArguments(Context context, String[] enteredArguments) {
		Arguments arguments = new Arguments();
		arguments.setAllArguments(Arrays.stream(enteredArguments).collect(Collectors.toList())); // TODO handle "quoted
																									// arguments"
		context.setArguments(arguments);
	}

	private void addLabel(Context context, String label) {
		Labels labels = new Labels();
		labels.setRoot(label);
		context.setLabels(labels);
	}

	private void run(Context context) {
		runner.apply(context)
			.exceptionally(errorHandler.asFutureHandler());
	}

}
