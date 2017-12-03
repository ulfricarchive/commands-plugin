package com.ulfric.plugin.commands.internal;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.ulfric.plugin.commands.Context;

final class Runner implements Function<Context, CompletableFuture<Void>> {

	private final CommandRegistry registry;
	private final Executor executor;

	Runner(CommandRegistry registry, Executor executor) {
		this.registry = registry;
		this.executor = executor;
	}

	@Override
	public CompletableFuture<Void> apply(Context context) {
		return CompletableFuture.runAsync(() -> registry.dispatch(context), executor);
	}

}
