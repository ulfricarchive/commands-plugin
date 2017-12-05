package com.ulfric.plugin.commands.stage;

import java.util.IdentityHashMap;
import java.util.Map;

import org.bukkit.event.Listener;

import com.ulfric.plugin.commands.Command;

public abstract class Stage<T> implements Listener {

	protected final Map<Class<? extends Command>, T> commandToContext = new IdentityHashMap<>();

	protected final T get(Class<? extends Command> command) {
		return commandToContext.computeIfAbsent(command, this::compute);
	}

	protected abstract T compute(Class<? extends Command> command);

}
