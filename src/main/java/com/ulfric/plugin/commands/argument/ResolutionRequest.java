package com.ulfric.plugin.commands.argument;

import com.ulfric.commons.value.Bean;
import com.ulfric.plugin.commands.Command;
import com.ulfric.plugin.commands.Context;

public class ResolutionRequest extends Bean {

	private ArgumentDefinition definition;
	private Context context;
	private String argument;
	private Class<? extends Command> command;

	public ArgumentDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(ArgumentDefinition definition) {
		this.definition = definition;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String getArgument() {
		return argument;
	}

	public void setArgument(String argument) {
		this.argument = argument;
	}

	public Class<? extends Command> getCommand() {
		return command;
	}

	public void setCommand(Class<? extends Command> command) {
		this.command = command;
	}

}
