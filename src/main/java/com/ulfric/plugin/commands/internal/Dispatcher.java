package com.ulfric.plugin.commands.internal;

import org.bukkit.command.CommandSender;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import com.ulfric.commons.bukkit.command.CommandSenderHelper;
import com.ulfric.commons.collection.MapHelper;
import com.ulfric.commons.time.TemporalHelper;
import com.ulfric.i18n.content.Details;
import com.ulfric.plugin.commands.Context;
import com.ulfric.plugin.commands.Invoker;
import com.ulfric.plugin.commands.Labels;
import com.ulfric.plugin.commands.MissingPermissionException;
import com.ulfric.plugin.commands.MustBePlayerException;
import com.ulfric.plugin.commands.argument.Arguments;
import com.ulfric.plugin.commands.argument.MissingArgumentException;
import com.ulfric.plugin.locale.TellService;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

final class Dispatcher extends org.bukkit.command.Command {

	private static final Executor EXECUTOR =
			Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("command-%d").build()); // TODO Fibers with Quasar
	private static final ConcurrentMap<UUID, Context> CURRENTLY_EXECUTING = MapHelper.newConcurrentMap(3);

	private final CommandRegistry registry;
	final Invoker command;

	Dispatcher(CommandRegistry registry, Invoker command) {
		super(command.getName(), command.getDescription(), command.getUsage(), command.getAliases());
		this.registry = registry;
		this.command = command;
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] arguments) {
		if (!command.shouldBypassRunningCommand()) {
			Context existingExecution = getRunningCommand(sender);
			if (existingExecution != null) {
				TellService.sendMessage(sender, "command-already-running", Details.of("running", existingExecution));
				return true;
			}
		}

		Context context = createContext(sender, label, arguments);

		if (command.shouldRunOnMainThread()) {
			this.run(context);
		} else {
			this.runAsync(context);
		}

		return true;
	}

	private Context getRunningCommand(CommandSender sender) {
		UUID uniqueId = CommandSenderHelper.getUniqueId(sender);
		if (uniqueId != null) {
			return CURRENTLY_EXECUTING.get(uniqueId);
		}
		return null;
	}

	private Context createContext(CommandSender sender, String label, String[] arguments) {
		Context context = new Context();
		context.setCreation(TemporalHelper.instantNow());
		context.setSender(sender);
		context.setCommand(command);
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
		arguments.setAllArguments(Arrays.stream(enteredArguments).collect(Collectors.toList())); // TODO handle "quoted arguments"
		context.setArguments(arguments);
	}

	private void addLabel(Context context, String label) {
		Labels labels = new Labels();
		labels.setRoot(label);
		context.setLabels(labels);
	}

	private void runAsync(Context context) {
		EXECUTOR.execute(() -> this.run(context));
	}

	private void run(Context context) {
		try {
			registry.dispatch(context);
		} catch (MissingPermissionException permissionCheck) {
			TellService.sendMessage(context.getSender(), "command-no-permission",
					Details.of("node", permissionCheck.getMessage()));
		} catch (MissingArgumentException requiredArgument) {
			TellService.sendMessage(context.getSender(), "command-missing-argument",
					Details.of("argument", requiredArgument.getMessage()));
		} catch (MustBePlayerException mustBePlayer) {
			TellService.sendMessage(context.getSender(), "command-must-be-player",
					Details.of("sender", mustBePlayer.getMessage()));
		} catch (Exception exception) {
			TellService.sendMessage(context.getSender(), "command-failed-execution");
			throw new CommandExecutionException(exception);
		}
	}

}
