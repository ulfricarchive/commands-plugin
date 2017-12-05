package com.ulfric.plugin.commands.function.defaults;

import com.ulfric.dragoon.application.Container;

public class DefaultFunctionsContainer extends Container {

	public DefaultFunctionsContainer() {
		install(SenderFunction.class);
		install(SenderToDisplayNameFunction.class);
	}

}
