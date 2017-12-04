package com.ulfric.plugin.commands;

import java.util.Objects;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.ulfric.plugin.commands.exception.CommandException;

public class CommandPreRunEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	private final Context context;
	private final Class<? extends Command> commandType;
	private CommandException failure;

	public CommandPreRunEvent(Context context, Class<? extends Command> commandType) {
		Objects.requireNonNull(context, "context");
		Objects.requireNonNull(commandType, "commandType");

		this.context = context;
		this.commandType = commandType;
	}

	public Context getContext() {
		return context;
	}

	public Class<? extends Command> getCommandType() {
		return commandType;
	}

	public void cancel(CommandException failure) {
		Objects.requireNonNull(failure, "failure");

		this.failure = failure;
	}

	public CommandException getFailure() {
		return failure;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

}
