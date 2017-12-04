package com.ulfric.plugin.commands.confirmation;

import com.ulfric.plugin.commands.Context;
import com.ulfric.plugin.commands.exception.CommandException;

public class ConfirmationRequiredException extends CommandException {

	private String confirmationMessage;

	public ConfirmationRequiredException(Context context, String confirmationMessage) {
		super(context);
		this.confirmationMessage = confirmationMessage;
	}

	public String getConfirmationMessage() {
		return confirmationMessage;
	}

}
