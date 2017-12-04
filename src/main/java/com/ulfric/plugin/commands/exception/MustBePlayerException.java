package com.ulfric.plugin.commands.exception;

import com.ulfric.plugin.commands.Context;

public class MustBePlayerException extends CommandException {

	public MustBePlayerException(Context context) {
		super(context);
	}

}
