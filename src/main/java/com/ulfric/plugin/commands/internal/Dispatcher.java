package com.ulfric.plugin.commands.internal;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

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

	private final CommandRegistry registry;
	final Invoker command;

	Dispatcher(CommandRegistry registry, Invoker command) {
		super(command.getName(), command.getDescription(), command.getUsage(), command.getAliases());
		this.registry = registry;
		this.command = command;
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] arguments) {
		Context context = createContext(sender, label, arguments);

		run(context);

		return true;
	}

	private Context createContext(CommandSender sender, String label, String[] arguments) {
		Context context = new Context();
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
		arguments.setAllArguments(Arrays.stream(enteredArguments).collect(Collectors.toList())); // TODO handle "quoted arguments"
		context.setArguments(arguments);
	}

	private void addLabel(Context context, String label) {
		Labels labels = new Labels();
		labels.setRoot(label);
		context.setLabels(labels);
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
		} catch (CommandException exit) {
			TellService.sendMessage(context.getSender(), exit.getMessage());
		} catch (Exception exception) {
			TellService.sendMessage(context.getSender(), "command-failed-execution");
			throw new CommandExecutionException(exception);
		}
	}

}
