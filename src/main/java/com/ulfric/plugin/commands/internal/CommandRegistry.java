package com.ulfric.plugin.commands.internal;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.SimplePluginManager;

import com.ulfric.commons.reflect.FieldHelper;
import com.ulfric.dragoon.reflect.Classes;
import com.ulfric.plugin.commands.Command;
import com.ulfric.plugin.commands.Context;
import com.ulfric.plugin.commands.Invoker;
import com.ulfric.plugin.commands.SkeletalRegistry;
import com.ulfric.tryto.TryTo;

import java.lang.reflect.Field;
import java.util.Objects;

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
	public void register(Command command) {
		Objects.requireNonNull(command, "command");

		Invoker invoker = asInvoker(command);
		invoker.registerWithParent();

		if (invoker.isRoot()) {
			Dispatcher dispatcher = new Dispatcher(this, invoker);
			bukkitRegistry.register(dispatcher.getName(), dispatcher);
		}
	}

	@Override
	public void unregister(Command command) {
		Objects.requireNonNull(command, "command");

		Invoker invoker = asInvoker(command);
		invoker.unregisterWithParent();

		if (invoker.isRoot()) {
			org.bukkit.command.Command bukkitCommand = bukkitRegistry.getCommand(invoker.getName());
			if (bukkitCommand instanceof Dispatcher && ((Dispatcher) bukkitCommand).command == invoker) {
				bukkitCommand.unregister(bukkitRegistry);
			}
		}
	}

	@Override
	public Command getCommand(String name) {
		Objects.requireNonNull(name, "name");

		org.bukkit.command.Command bukkitCommand = bukkitRegistry.getCommand(name.toLowerCase());
		if (bukkitCommand instanceof Dispatcher) {
			return ((Dispatcher) bukkitCommand).command;
		}
		return null;
	}

	private Invoker asInvoker(Command command) {
		if (command instanceof Invoker) {
			return (Invoker) command;
		}

		@SuppressWarnings("unchecked")
		Class<? extends Command> type = (Class<? extends Command>) Classes.getNonDynamic(command.getClass());
		return Invoker.of(type);
	}

	@Override
	public void dispatch(Context context) {
		super.dispatch(context);
	}

}
