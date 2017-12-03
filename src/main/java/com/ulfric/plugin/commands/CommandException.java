package com.ulfric.plugin.commands;

public class CommandException extends RuntimeException {

	private final Context context;

	public CommandException(Context context, String message) {
		super(message);

		this.context = context;
	}

	public final Context getContext() {
		return context;
	}

}