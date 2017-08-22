package com.ulfric.andrew;

import org.bukkit.command.CommandSender;

import com.ulfric.commons.value.Bean;

import java.util.List;
import java.util.Map;

public class Context extends Bean {

	private CommandSender sender;
	private String label;
	private Map<Class<? extends Command>, List<String>> arguments;
	private Command command;

	public CommandSender getSender() {
		return sender;
	}

	public void setSender(CommandSender sender) {
		this.sender = sender;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Map<Class<? extends Command>, List<String>> getArguments() {
		return arguments;
	}

	public void setArguments(Map<Class<? extends Command>, List<String>> arguments) {
		this.arguments = arguments;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

}
