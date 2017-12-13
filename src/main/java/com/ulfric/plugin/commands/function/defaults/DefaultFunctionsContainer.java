package com.ulfric.plugin.commands.function.defaults;

import com.ulfric.dragoon.application.Container;

public class DefaultFunctionsContainer extends Container {

	public DefaultFunctionsContainer() {
		install(ContextToCommandSenderFunction.class);
		install(CommandSenderToDisplayNameFunction.class);
	}

}
