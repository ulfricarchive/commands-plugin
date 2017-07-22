package com.ulfric.andrew.argument;

import com.ulfric.andrew.CommandException;

public class MissingArgumentException extends CommandException {

	public MissingArgumentException(String name) {
		super(name);
	}

}