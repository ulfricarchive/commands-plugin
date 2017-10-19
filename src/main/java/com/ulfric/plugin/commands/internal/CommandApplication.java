package com.ulfric.plugin.commands.internal;

import com.ulfric.dragoon.application.Application;
import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.reflect.Classes;
import com.ulfric.plugin.commands.Command;
import com.ulfric.plugin.commands.Invoker;
import com.ulfric.plugin.commands.Registry;

import java.util.Objects;

public class CommandApplication extends Application {

	private final Invoker command;

	@Inject
	private Registry registry;

	public CommandApplication(Command command) {
		Objects.requireNonNull(command, "command");

		@SuppressWarnings("unchecked")
		Class<? extends Command> commandType = (Class<? extends Command>) Classes.getNonDynamic(command.getClass());
		this.command = Invoker.of(commandType);

		addBootHook(this::register);
		addShutdownHook(this::unregister);
	}

	private void register() {
		registry.register(command);
	}

	private void unregister() {
		registry.unregister(command);
	}

}