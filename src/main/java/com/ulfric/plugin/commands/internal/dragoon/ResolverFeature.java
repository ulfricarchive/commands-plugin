package com.ulfric.plugin.commands.internal.dragoon;

import com.ulfric.dragoon.application.Application;
import com.ulfric.dragoon.application.Feature;
import com.ulfric.plugin.commands.argument.Resolver;

public class ResolverFeature extends Feature {

	@Override
	public Application apply(Object resolver) {
		if (resolver instanceof Resolver) {
			return new ResolverApplication((Resolver<?>) resolver);
		}

		return null;
	}

}