package com.ulfric.andrew;

import org.bukkit.command.CommandSender;

import com.ulfric.commons.value.Bean;

import java.time.Instant;

public class Context extends Bean {

	private CommandSender sender;
	private Arguments arguments;
	private Labels labels;
	private Command command;
	private Instant creation;

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

}
