package com.ulfric.andrew;

public class MissingPermissionException extends CommandException {

	public MissingPermissionException() {
		super("command-no-permission");
	}

}