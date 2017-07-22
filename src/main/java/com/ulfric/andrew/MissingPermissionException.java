package com.ulfric.andrew;

public class MissingPermissionException extends CommandException {

	public MissingPermissionException(String node) {
		super(node);
	}

}