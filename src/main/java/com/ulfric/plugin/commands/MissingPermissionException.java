package com.ulfric.plugin.commands;

public class MissingPermissionException extends CommandException {

	public MissingPermissionException(String node) {
		super(node);
	}

}