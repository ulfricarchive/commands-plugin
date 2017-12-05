package com.ulfric.plugin.commands.stage;

import com.ulfric.plugin.commands.Command;

public class VoidStage extends Stage<Void> {

	@Override
	protected Void compute(Class<? extends Command> command) {
		return null;
	}

}
