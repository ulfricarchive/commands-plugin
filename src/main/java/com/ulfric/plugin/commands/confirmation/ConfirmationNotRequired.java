package com.ulfric.plugin.commands.confirmation;

import java.util.UUID;

public enum ConfirmationNotRequired implements Confirmation {

	INSTANCE;

	@Override
	public boolean test(UUID ignore) {
		return true;
	}

}
