package com.ulfric.plugin.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.google.common.truth.Truth;

import com.ulfric.plugin.commands.Command;
import com.ulfric.plugin.commands.Context;
import com.ulfric.plugin.commands.Invoker;
import com.ulfric.plugin.commands.SkeletalRegistry;
import com.ulfric.veracity.Veracity;

import java.util.Arrays;

class SkeletalRegistryTest extends ContextTestSuite {

	private SkeletalRegistry registry;

	@BeforeEach
	void setup() {
		registry = Mockito.mock(SkeletalRegistry.class);
		Mockito.doCallRealMethod().when(registry).dispatch(ArgumentMatchers.any());
	}

	@Test
	void testCommandNotFound() {
		Mockito.when(registry.getCommand(ArgumentMatchers.anyString())).thenReturn(null);
		Veracity.assertThat(() -> registry.dispatch(context)).runsWithoutExceptions();
	}

	@Test
	void testCommandWithoutArguments() {
		Invoker command = Invoker.of(Hello.class);
		Mockito.when(registry.getCommand(ArgumentMatchers.anyString())).thenReturn(command);
		registry.dispatch(context);
		Truth.assertThat(context).isSameAs(Hello.last);
	}

	@Test
	void testCommand() {
		context.getArguments().getAllArguments().addAll(Arrays.asList("one", "two", "three"));
		Invoker command = Invoker.of(Hello.class);
		Mockito.when(registry.getCommand(ArgumentMatchers.anyString())).thenReturn(command);
		registry.dispatch(context);
		Truth.assertThat(context).isSameAs(Hello.last);
	}

	@Test
	void testSubCommand() {
		context.getArguments().getAllArguments().add("world");
		Invoker command = Invoker.of(Hello.class);
		Invoker.of(World.class).registerWithParent();
		Mockito.when(registry.getCommand(ArgumentMatchers.anyString())).thenReturn(command);
		registry.dispatch(context);
		Truth.assertThat(context).isSameAs(World.last);
		Invoker.of(World.class).unregisterWithParent();
	}

	@Test
	void testSubCommandBothHaveArguments() {
		context.getArguments().getAllArguments().addAll(Arrays.asList("helloarg", "world", "worldarg"));
		Invoker command = Invoker.of(Hello.class);
		Invoker.of(World.class).registerWithParent();
		Mockito.when(registry.getCommand(ArgumentMatchers.anyString())).thenReturn(command);
		registry.dispatch(context);
		Truth.assertThat(World.last.getArguments().getArguments().get(Hello.class)).containsExactly("helloarg");
		Truth.assertThat(World.last.getArguments().getArguments().get(World.class)).containsExactly("worldarg");
		Invoker.of(World.class).unregisterWithParent();
	}

	@Test
	void testInstantiate() {
		Veracity.assertThat(() -> {
			new SkeletalRegistry() {
				@Override
				public void register(Invoker command) {
				}

				@Override
				public void unregister(Invoker command) {
				}

				@Override
				public Invoker getCommand(String name) {
					return null;
				}
			}.hashCode();
		}).runsWithoutExceptions();
	}

	static class Hello extends Command {
		static Context last;

		@Override
		public void run() {
			last = context;
		}
	}

	static class World extends Hello {
		static Context last;

		@Override
		public void run() {
			last = context;
		}
	}

}