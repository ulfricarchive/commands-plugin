package com.ulfric.plugin.commands;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ulfric.commons.value.Bean;
import com.ulfric.plugin.commands.argument.Arguments;
import com.ulfric.plugin.commands.exception.MustBePlayerException;

public class Context extends Bean {

	public static Player getPlayer(Context context) {
		CommandSender sender = context.getSender();

		if (sender instanceof Player) {
			return (Player) sender;
		}

		throw new MustBePlayerException(context);
	}

	private CommandSender sender;
	private Arguments arguments;
	private Command command;
	private UUID executionId;

	public CommandSender getSender() {
		return sender;
	}

	public void setSender(CommandSender sender) {
		this.sender = sender;
	}

	public Arguments getArguments() {
		return arguments;
	}

	public void setArguments(Arguments arguments) {
		this.arguments = arguments;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public UUID getExecutionId() {
		return executionId;
	}

	public void setExecutionId(UUID executionId) {
		this.executionId = executionId;
	}

}
