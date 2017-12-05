package com.ulfric.plugin.commands;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface CommandExtension {

	CommandSender sender();

	Player player();

	UUID uniqueId();

}
