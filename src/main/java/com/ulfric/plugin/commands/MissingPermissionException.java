package com.ulfric.plugin.commands;

public class MissingPermissionException extends CommandException {

	private final String permissionMessage;

	public MissingPermissionException(Context context, String node, String permissionMessage) {
		super(context, node);

		this.permissionMessage = permissionMessage;
	}

	public String getPermissionMessage() {
		return permissionMessage;
	}

}