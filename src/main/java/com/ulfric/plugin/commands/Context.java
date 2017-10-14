package com.ulfric.plugin.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ulfric.commons.value.Bean;
import com.ulfric.plugin.commands.argument.Arguments;

import java.time.Instant;

public class Context extends Bean {

	public static Player getPlayer(Context context) {
		CommandSender sender = context.getSender();

		if (sender instanceof Player) {
			return (Player) sender;
		}

		throw new MustBePlayerException(sender);
	}

	private CommandSender sender;
	private Arguments arguments;
	private Labels labels;
	private Command command;
	private Instant creation;
	private String commandLine;
	private Lock lock;

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

	public Labels getLabels() {
		return labels;
	}

	public void setLabels(Labels labels) {
		this.labels = labels;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public Instant getCreation() {
		return creation;
	}

	public void setCreation(Instant creation) {
		this.creation = creation;
	}

	public String getCommandLine() {
		return commandLine;
	}

	public void setCommandLine(String commandLine) {
		this.commandLine = commandLine;
	}

	public Lock getLock() {
		return lock;
	}

	public void setLock(Lock lock) {
		this.lock = lock;
	}

}
