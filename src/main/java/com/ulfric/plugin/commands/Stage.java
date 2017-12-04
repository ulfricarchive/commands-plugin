package com.ulfric.plugin.commands;

import java.util.IdentityHashMap;
import java.util.Map;

import org.bukkit.event.Listener;

public class Stage<T> implements Listener {

	protected final Map<Class<? extends Command>, T> commandToContext = new IdentityHashMap<>();

}
