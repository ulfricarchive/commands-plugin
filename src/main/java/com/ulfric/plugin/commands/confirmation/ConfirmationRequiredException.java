package com.ulfric.plugin.commands.confirmation;

import com.ulfric.plugin.commands.CommandException;
import com.ulfric.plugin.commands.Context;

public class ConfirmationRequiredException extends CommandException {

	public ConfirmationRequiredException(Context context, String message) {
		super(context, message);
	}

}
