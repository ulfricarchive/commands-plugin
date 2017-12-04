package com.ulfric.plugin.commands.internal;

import java.lang.reflect.Field;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.SimplePluginManager;

import com.ulfric.commons.reflect.FieldHelper;
import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.plugin.Plugin;
import com.ulfric.tryto.TryTo;

public class Registry {

	@Inject
	private ObjectFactory factory;

	private final CommandMap bukkitRegistry = lookupBukkitRegistry();

	private CommandMap lookupBukkitRegistry() {
		Field field = FieldHelper.getDeclaredField(SimplePluginManager.class, "commandMap")
				.orElseThrow(NullPointerException::new);
		field.setAccessible(true);

		return (CommandMap) TryTo.get(() -> field.get(Bukkit.getPluginManager()));
	}

	public void register(Invoker command) {
		Objects.requireNonNull(command, "command");

		if (!command.registerWithParent()) {
			PluginCommand bukkitCommand =
					factory.request(PluginCommand.class, command.getName(), Plugin.getProvidingPlugin(command.getCommand()));
			bukkitCommand.setAliases(command.getAliases());
			bukkitCommand.setExecutor(command);
			bukkitRegistry.register(bukkitCommand.getName(), bukkitCommand);
		}
	}

	public void unregister(Invoker command) {
		Objects.requireNonNull(command, "command");

		if (!command.unregisterWithParent()) {
			org.bukkit.command.Command bukkitGenericCommand = bukkitRegistry.getCommand(command.getName());
			if (bukkitGenericCommand instanceof PluginCommand) {
				PluginCommand pluginCommand = (PluginCommand) bukkitGenericCommand;
				if (pluginCommand.getExecutor() == command) {
					pluginCommand.unregister(bukkitRegistry);
				}
			}
		}
	}

}
