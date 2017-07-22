package com.ulfric.andrew;

import com.ulfric.commons.value.Bean;

import java.util.List;
import java.util.Map;

public class Context extends Bean {

	private Sender sender;
	private String label;
	private Map<Class<? extends Command>, List<String>> arguments;
	private Command command;

	public Sender getSender() {
		return sender;
	}

	public void setSender(Sender sender) {
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
