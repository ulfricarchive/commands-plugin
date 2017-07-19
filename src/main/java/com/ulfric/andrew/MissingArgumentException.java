package com.ulfric.andrew;

public class MissingArgumentException extends CommandException {

	public MissingArgumentException(String name) {
		super(name);
	}

}