package com.ulfric.plugin.commands.internal.dragoon;

import java.util.Objects;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.application.Application;
import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.extension.postconstruct.PostConstruct;
import com.ulfric.dragoon.reflect.Classes;
import com.ulfric.plugin.commands.Command;
import com.ulfric.plugin.commands.internal.Invoker;
import com.ulfric.plugin.commands.internal.Registry;

public class CommandApplication extends Application {

	private final Class<? extends Command> commandType;
	private Invoker command;

	@Inject
	private Registry registry;

	@Inject
	private ObjectFactory factory;

	public CommandApplication(Command command) {
		Objects.requireNonNull(command, "command");

		this.commandType = Classes.getNonDynamic(command.getClass()).asSubclass(Command.class);

		addBootHook(this::register);
		addShutdownHook(this::unregister);
	}

	@PostConstruct
	private void createInvoker() {
		this.command = factory.request(Invoker.class, commandType);
	}

	private void register() {
		registry.register(command);
	}

	private void unregister() {
		registry.unregister(command);
	}

}