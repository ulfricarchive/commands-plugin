package com.ulfric.plugin.commands.exception;

import com.ulfric.plugin.commands.Context;

public class CommandException extends RuntimeException {

	private final Context context;

	public CommandException(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

}
