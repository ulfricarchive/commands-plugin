package com.ulfric.plugin.commands.dragoon;

import com.ulfric.dragoon.application.Application;
import com.ulfric.plugin.commands.argument.Resolver;

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