package com.ulfric.andrew;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public abstract class Resolver<T> implements BiFunction<ArgumentDefinition, String, T> {

	private static final Map<Type, List<Resolver<?>>> RESOLVERS = new HashMap<>();

	static {
		register(new IdentityResolver());
	}

	public static void register(Resolver<?> resolver) {
		Objects.requireNonNull(resolver, "resolver");

		RESOLVERS.computeIfAbsent(resolver.getType(), ignore -> new ArrayList<>()).add(resolver);
	}

	public static void remove(Resolver<?> resolver) {
		Objects.requireNonNull(resolver, "resolver");

		List<Resolver<?>> resolvers = RESOLVERS.get(resolver.getType());
		if (resolvers == null) {
			return;
		}
		resolvers.remove(resolver);
	}

	public static Object resolve(ArgumentDefinition definition, String argument) {
		List<Resolver<?>> resolvers = RESOLVERS.get(definition.getType());
		if (resolvers == null) {
			return null;
		}

		for (Resolver<?> resolver : resolvers) {
			Object resolved = resolver.apply(definition, argument);
			if (resolved != null) {
				return resolved;
			}
		}

		return null;
	}

	private final Type type;

	public Resolver(Type type) {
		Objects.requireNonNull(type, "type");

		this.type = type;
	}

	public final Type getType() {
		return type;
	}

}