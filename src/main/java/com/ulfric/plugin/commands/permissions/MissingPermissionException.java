package com.ulfric.plugin.commands.permissions;

import com.ulfric.plugin.commands.Context;
import com.ulfric.plugin.commands.exception.CommandException;

public class MissingPermissionException extends CommandException {

	private final String permissionMessage;
	private final String permissionNode;

	public MissingPermissionException(Context context, String permissionMessage, String permissionNode) {
		super(context);

		this.permissionMessage = permissionMessage;
		this.permissionNode = permissionNode;
	}

	public String getPermissionMessage() {
		return permissionMessage;
	}

	public String getPermissionNode() {
		return permissionNode;
	}

}