package com.ulfric.plugin.commands.internal;

import java.lang.reflect.Field;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.SimplePluginManager;

import com.ulfric.commons.reflect.FieldHelper;
import com.ulfric.plugin.commands.Invoker;
import com.ulfric.plugin.commands.SkeletalRegistry;
import com.ulfric.tryto.TryTo;

public class CommandRegistry extends SkeletalRegistry {

	private final CommandMap bukkitRegistry;

	public CommandRegistry() {
		bukkitRegistry = lookupBukkitRegistry();
	}

	private CommandMap lookupBukkitRegistry() {
		Field field = FieldHelper.getDeclaredField(SimplePluginManager.class, "commandMap")
				.orElseThrow(NullPointerException::new);
		field.setAccessible(true);

		return (CommandMap) TryTo.get(() -> field.get(Bukkit.getPluginManager()));
	}

	@Override
	public void register(Invoker command) {
		Objects.requireNonNull(command, "command");

		command.registerWithParent();

		if (command.isRoot()) {
			Dispatcher dispatcher = new Dispatcher(this, command);
			bukkitRegistry.register(dispatcher.getName(), dispatcher);
		}
	}

	@Override
	public void unregister(Invoker command) {
		Objects.requireNonNull(command, "command");

		command.unregisterWithParent();

		if (command.isRoot()) {
			org.bukkit.command.Command bukkitCommand = bukkitRegistry.getCommand(command.getName());
			if (bukkitCommand instanceof Dispatcher && ((Dispatcher) bukkitCommand).command == command) {
				bukkitCommand.unregister(bukkitRegistry);
			}
		}
	}

	@Override
	public Invoker getCommand(String name) {
		Objects.requireNonNull(name, "name");

		org.bukkit.command.Command bukkitCommand = bukkitRegistry.getCommand(name.toLowerCase());
		if (bukkitCommand instanceof Dispatcher) {
			return ((Dispatcher) bukkitCommand).command;
		}
		return null;
	}

}
