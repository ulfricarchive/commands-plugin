package com.ulfric.plugin.commands.argument;

import com.ulfric.plugin.commands.CommandException;

public class MissingArgumentException extends CommandException {

	public MissingArgumentException(String name) {
		super(name);
	}

}