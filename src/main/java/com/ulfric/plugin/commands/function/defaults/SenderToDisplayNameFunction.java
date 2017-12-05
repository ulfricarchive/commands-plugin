package com.ulfric.plugin.commands.function.defaults;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ulfric.i18n.function.Function;

public class SenderToDisplayNameFunction extends Function<CommandSender> {

	public SenderToDisplayNameFunction() {
		super("displayName", CommandSender.class);
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
