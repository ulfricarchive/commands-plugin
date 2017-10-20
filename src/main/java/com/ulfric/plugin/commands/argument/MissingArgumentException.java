package com.ulfric.plugin.commands.argument;

import com.ulfric.plugin.commands.CommandException;

public class MissingArgumentException extends CommandException {

	private String argumentMessage;

	public MissingArgumentException(String name, String argumentMessage) {
		super(name);

		this.argumentMessage = argumentMessage;
	}

	public String getArgumentMessage() {
		return argumentMessage;
	}

}