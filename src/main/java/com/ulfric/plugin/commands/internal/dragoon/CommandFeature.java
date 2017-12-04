package com.ulfric.plugin.commands.internal.dragoon;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.application.Application;
import com.ulfric.dragoon.application.Feature;
import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.plugin.commands.Command;

public class CommandFeature extends Feature {

	@Inject
	private ObjectFactory factory;

	@Override
	public Application apply(Object object) {
		if (object instanceof Command) {
			return factory.request(CommandApplication.class, object);
		}
		return null;
	}

}
