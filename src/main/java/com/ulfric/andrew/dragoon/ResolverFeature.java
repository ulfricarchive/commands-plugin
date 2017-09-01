package com.ulfric.andrew.dragoon;

import com.ulfric.andrew.argument.Resolver;
import com.ulfric.dragoon.application.Application;
import com.ulfric.dragoon.application.Feature;

public class ResolverFeature extends Feature { // TODO move to andrew codebase

	@Override
	public Application apply(Object resolver) {
		if (resolver instanceof Resolver) {
			return new ResolverApplication((Resolver<?>) resolver);
		}

		return null;
	}

}