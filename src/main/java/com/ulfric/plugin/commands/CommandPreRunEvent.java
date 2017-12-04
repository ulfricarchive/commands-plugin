package com.ulfric.plugin.commands;

import java.util.Objects;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.ulfric.plugin.commands.exception.CommandException;

public class CommandPreRunEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	private final Context context;
	private boolean cancelled;
	private CommandException failure;

	public CommandPreRunEvent(Context context) {
		Objects.requireNonNull(context, "context");

		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public void cancel(CommandException failure) {
		Objects.requireNonNull(failure, "failure");

		setCancelled(true);
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
