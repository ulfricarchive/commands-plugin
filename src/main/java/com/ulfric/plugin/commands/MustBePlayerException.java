package com.ulfric.plugin.commands;

import org.bukkit.command.CommandSender;

public class MustBePlayerException extends RuntimeException {

	public MustBePlayerException(CommandSender sender) {
		super(sender.getName());
	}

}