package com.ulfric.plugin.commands.argument;

import com.ulfric.plugin.commands.Context;
import com.ulfric.plugin.commands.exception.CommandException;

public class MissingArgumentException extends CommandException {

	private ArgumentDefinition definition;

	public MissingArgumentException(Context context, ArgumentDefinition definition) {
		super(context);

		this.definition = definition;
	}

	public ArgumentDefinition getDefinition() {
		return definition;
	}

}