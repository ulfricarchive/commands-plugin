package com.ulfric.plugin.commands.confirmation;

import com.ulfric.plugin.commands.CommandException;

public class ConfirmationRequiredException extends CommandException {

	public ConfirmationRequiredException(String message) {
		super(message);
	}

}
