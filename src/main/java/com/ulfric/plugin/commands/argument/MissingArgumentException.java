package com.ulfric.plugin.commands.argument;

import com.ulfric.plugin.commands.CommandException;
import com.ulfric.plugin.commands.Context;

public class MissingArgumentException extends CommandException {

	private String argumentMessage;

	public MissingArgumentException(Context context, String name, String argumentMessage) {
		super(context, name);

		this.argumentMessage = argumentMessage;
	}

	public String getArgumentMessage() {
		return argumentMessage;
	}

}