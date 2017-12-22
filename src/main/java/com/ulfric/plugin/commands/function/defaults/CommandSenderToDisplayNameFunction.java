package com.ulfric.plugin.commands.function.defaults;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ulfric.plugin.locale.function.CommandSenderFunction;

public class CommandSenderToDisplayNameFunction extends CommandSenderFunction {

	public CommandSenderToDisplayNameFunction() {
		super("displayName");
	}

	@Override
	public Object apply(CommandSender sender) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			return player.getDisplayName();
		}

		return sender.getName();
	}

}
