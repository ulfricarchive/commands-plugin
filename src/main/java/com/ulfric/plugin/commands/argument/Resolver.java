package com.ulfric.plugin.commands.argument;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import com.ulfric.commons.collection.Computations;
import com.ulfric.plugin.commands.argument.defaults.ContextResolver;
import com.ulfric.plugin.commands.argument.defaults.IdentityResolver;
import com.ulfric.plugin.commands.argument.defaults.IntegerResolver;
import com.ulfric.plugin.commands.argument.defaults.MatchResolver;
import com.ulfric.plugin.commands.argument.defaults.SlugResolver;

public abstract class Resolver<T> implements Function<ResolutionRequest, T> {

	private static final Map<Type, List<Resolver<?>>> RESOLVERS = new HashMap<>();

	static {
		register(new ContextResolver());
		register(new IdentityResolver());
		register(new SlugResolver());
		register(new MatchResolver());
		register(new IntegerResolver());
	}

	public static void register(Resolver<?> resolver) {
		Objects.requireNonNull(resolver, "resolver");

		resolver.getTypes().forEach(type ->
			RESOLVERS.computeIfAbsent(type, Computations::newArrayListIgnoring).add(resolver)
		);
	}

	public static void remove(Resolver<?> resolver) {
		Objects.requireNonNull(resolver, "resolver");

		resolver.getTypes().forEach(type -> {
			List<Resolver<?>> resolvers = RESOLVERS.get(type);
			if (resolvers == null) {
				return;
			}
			resolvers.remove(resolver);
			if (resolvers.isEmpty()) {
				RESOLVERS.remove(type);
			}
		});
	}

	public static Object resolve(ResolutionRequest request) {
		List<Resolver<?>> resolvers = RESOLVERS.get(request.getDefinition().getType());
		if (resolvers == null) {
			return null;
		}

		for (Resolver<?> resolver : resolvers) {
			Object resolved = resolver.apply(request);
			if (resolved != null) {
				return resolved;
			}
		}

		return null;
	}

	private final List<Type> types;

	public Resolver(Type type) {
		Objects.requireNonNull(type, "type");

		this.types = Collections.singletonList(type);
	}

	public Resolver(Type first, Type... additional) {
		Objects.requireNonNull(first, "first");
		Objects.requireNonNull(additional, "additional");

		List<Type> types = new ArrayList<>(additional.length + 1);
		types.add(first);
		Arrays.stream(additional)
			.forEach(types::add);
		this.types = Collections.unmodifiableList(types);
	}

	public final List<Type> getTypes() {
		return types;
	}

}