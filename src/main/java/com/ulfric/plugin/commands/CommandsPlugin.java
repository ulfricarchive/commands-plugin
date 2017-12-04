package com.ulfric.plugin.commands;

import com.ulfric.plugin.Plugin;

public class CommandsPlugin extends Plugin {

	public CommandsPlugin() {
		install(CommandsContainer.class);
	}

}
