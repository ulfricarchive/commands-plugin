package com.ulfric.andrew.dragoon;

import com.ulfric.andrew.argument.Resolver;
import com.ulfric.dragoon.application.Application;

import java.util.Objects;

public class ResolverApplication extends Application { // TODO move to andrew codebase

	private final Resolver<?> resolver;

	public ResolverApplication(Resolver<?> resolver) {
		Objects.requireNonNull(resolver, "resolver");

		this.resolver = resolver;

		addBootHook(this::register);
		addShutdownHook(this::unregister);
	}

	private void register() {
		Resolver.register(resolver);
	}

	private void unregister() {
		Resolver.remove(resolver);
	}

}