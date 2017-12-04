package com.ulfric.plugin.commands.permissions;

import com.ulfric.plugin.commands.Context;
import com.ulfric.plugin.commands.exception.CommandException;

public class MissingPermissionException extends CommandException {

	private final String permissionMessage;

	public MissingPermissionException(Context context, String permissionMessage) {
		super(context);

		this.permissionMessage = permissionMessage;
	}

	public String getPermissionMessage() {
		return permissionMessage;
	}

}