package com.ulfric.plugin.commands;

import org.bukkit.command.CommandSender;

public class MustBePlayerException extends CommandException {

	public MustBePlayerException(Context context, CommandSender sender) {
		super(context, sender.getName());
	}

}