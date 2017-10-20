package com.ulfric.plugin.commands;

import com.ulfric.plugin.Plugin;
import com.ulfric.plugin.commands.argument.defaults.DefaultResolversContainer;
import com.ulfric.plugin.commands.dragoon.ResolverFeature;
import com.ulfric.plugin.commands.internal.CommandFeature;
import com.ulfric.plugin.commands.internal.CommandRegistry;

public class CommandsPlugin extends Plugin {

	public CommandsPlugin() {
		install(ResolverFeature.class);
		install(CommandFeature.class);
		install(DefaultResolversContainer.class);

		FACTORY.bind(Registry.class).toSingleton(CommandRegistry.class);
	}

}
