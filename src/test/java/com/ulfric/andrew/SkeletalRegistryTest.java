package com.ulfric.andrew;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.google.common.truth.Truth;

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
	void testCommandNotInvoker() {
		Command command = Mockito.mock(Command.class);
		Mockito.when(registry.getCommand(ArgumentMatchers.anyString())).thenReturn(command);
		registry.dispatch(context);
		Mockito.verify(command, Mockito.times(1)).run(context);
	}

	@Test
	void testCommandWithoutArguments() {
		Command command = Invoker.of(Hello.class);
		Mockito.when(registry.getCommand(ArgumentMatchers.anyString())).thenReturn(command);
		registry.dispatch(context);
		Truth.assertThat(context).isSameAs(Hello.last);
	}

	@Test
	void testCommand() {
		context.getArguments().put(Command.class, Arrays.asList("one", "two", "three"));
		Command command = Invoker.of(Hello.class);
		Mockito.when(registry.getCommand(ArgumentMatchers.anyString())).thenReturn(command);
		registry.dispatch(context);
		Truth.assertThat(context).isSameAs(Hello.last);
	}

	@Test
	void testSubCommand() {
		context.getArguments().put(Command.class, Arrays.asList("world"));
		Command command = Invoker.of(Hello.class);
		Invoker.of(World.class).registerWithParent();
		Mockito.when(registry.getCommand(ArgumentMatchers.anyString())).thenReturn(command);
		registry.dispatch(context);
		Truth.assertThat(context).isSameAs(World.last);
		Invoker.of(World.class).unregisterWithParent();
	}

	@Test
	void testSubCommandBothHaveArguments() {
		context.getArguments().put(Command.class, Arrays.asList("helloarg", "world", "worldarg"));
		Command command = Invoker.of(Hello.class);
		Invoker.of(World.class).registerWithParent();
		Mockito.when(registry.getCommand(ArgumentMatchers.anyString())).thenReturn(command);
		registry.dispatch(context);
		Truth.assertThat(World.last.getArguments().get(Hello.class)).containsExactly("helloarg");
		Truth.assertThat(World.last.getArguments().get(World.class)).containsExactly("worldarg");
		Invoker.of(World.class).unregisterWithParent();
	}

	@Test
	void testInstantiate() {
		Veracity.assertThat(() -> {
			new SkeletalRegistry() {

				@Override
				public void register(Command command) {
				}

				@Override
				public void unregister(Command command) {
				}

				@Override
				public Command getCommand(String name) {
					return null;
				}
			}.hashCode();
		}).runsWithoutExceptions();
	}

	static class Hello implements Command {
		static Context last;

		@Override
		public void run(Context context) {
			last = context;
		}
	}

	static class World extends Hello {
		static Context last;

		@Override
		public void run(Context context) {
			last = context;
		}
	}

}