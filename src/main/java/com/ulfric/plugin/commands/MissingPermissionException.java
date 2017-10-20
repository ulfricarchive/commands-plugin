package com.ulfric.plugin.commands;

public class MissingPermissionException extends CommandException {

	private final String permissionMessage;

	public MissingPermissionException(String node, String permissionMessage) {
		super(node);

		this.permissionMessage = permissionMessage;
	}

	public String getPermissionMessage() {
		return permissionMessage;
	}

}