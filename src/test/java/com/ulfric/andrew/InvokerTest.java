package com.ulfric.andrew;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.google.common.truth.Truth;

import com.ulfric.andrew.argument.Argument;
import com.ulfric.andrew.argument.MissingArgumentException;
import com.ulfric.andrew.argument.ResolutionRequest;
import com.ulfric.andrew.argument.Resolver;
import com.ulfric.commons.naming.Name;
import com.ulfric.veracity.Veracity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@RunWith(JUnitPlatform.class)
class InvokerTest extends ContextTestSuite {

	@Test
	void testOfInvoker() {
		Veracity.assertThat(() -> Invoker.of(Invoker.class)).doesThrow(IllegalArgumentException.class);
	}

	@Test
	void testOfPermissionCheckFails() {
		Mockito.when(context.getSender().hasPermission(Matchers.anyString())).thenReturn(false);
		Veracity.assertThat(() -> Invoker.of(PermissionCheck.class).run(context))
			.doesThrow(MissingPermissionException.class);
	}

	@Test
	void testOfPermissionCheck() {
		Mockito.when(context.getSender().hasPermission(Matchers.anyString())).thenReturn(true);
		Veracity.assertThat(() -> Invoker.of(PermissionCheck.class).run(context))
			.runsWithoutExceptions();
	}

	@Test
	void testGetUsageOfNoUsage() {
		Truth.assertThat(Invoker.of(Hello.class).getUsage()).isEqualTo("/hello");
	}

	@Test
	void testGetUsageDescribed() {
		Truth.assertThat(Invoker.of(UsageDescribed.class).getUsage()).isEqualTo("/hello <your argument>");
	}

	@Test
	void testGetDescriptionOfNoUsage() {
		Truth.assertThat(Invoker.of(Hello.class).getDescription()).isEmpty();
	}

	@Test
	void testGetDescriptionDescribed() {
		Truth.assertThat(Invoker.of(DescriptionDescribed.class).getDescription()).isEqualTo("say hello");
	}

	@Test
	void testGetAliases() {
		Truth.assertThat(Invoker.of(Aliased.class).getAliases()).containsExactly("hi");
	}

	@Test
	void testIsAsyncByDefault() {
		Truth.assertThat(Invoker.of(Hello.class).shouldRunOnMainThread()).isFalse();
	}

	@Test
	void testIsSyncIfSpecified() {
		Truth.assertThat(Invoker.of(SyncCommand.class).shouldRunOnMainThread()).isTrue();
	}

	@Test
	void testGetInferredName() {
		Truth.assertThat(Invoker.of(Hello.class).getName()).isEqualTo("hello");
	}

	@Test
	void testGetInferredNameWithSuffix() {
		Truth.assertThat(Invoker.of(HelloCommand.class).getName()).isEqualTo("hello");
	}

	@Test
	void testGetGivenName() {
		Truth.assertThat(Invoker.of(HelloGiven.class).getName()).isEqualTo("greetings");
	}

	@Test
	void testIdentityArgument() {
		String name = UUID.randomUUID().toString();
		context.getArguments().put(HelloName.class, new ArrayList<>(Arrays.asList(name)));
		Invoker.of(HelloName.class).run(context);
		Truth.assertThat(HelloName.last).isEqualTo(name);
	}

	@Test
	void testIdentityArgumentRequiredButNotGiven() {
		Veracity.assertThat(() -> Invoker.of(HelloName.class).run(context)).doesThrow(MissingArgumentException.class);
	}

	@Test
	void testIdentityArgumentOptional() {
		Veracity.assertThat(() -> Invoker.of(HelloNameOptional.class).run(context)).runsWithoutExceptions();
	}

	@Test
	void testIdentityArgumentOptionalEmptyArguments() {
		context.getArguments().put(HelloNameOptional.class, new ArrayList<>());
		Veracity.assertThat(() -> Invoker.of(HelloNameOptional.class).run(context)).runsWithoutExceptions();
	}

	@Test
	void testSpecialCaseArgument() {
		Resolver<String> mr = new MrResolver();
		context.getArguments().put(HelloMr.class, new ArrayList<>(Arrays.asList("Mr. Johnson")));
		Resolver.register(mr);
		Invoker.of(HelloMr.class).run(context);
		Truth.assertThat(HelloMr.last).isEqualTo("Mr. Johnson");
		Resolver.remove(mr);
	}

	@Test
	void testSpecialCaseArgumentMissing() {
		Resolver<String> mr = new MrResolver();
		context.getArguments().put(HelloMr.class, new ArrayList<>());
		Resolver.register(mr);
		Veracity.assertThat(() -> Invoker.of(HelloMr.class).run(context)).doesThrow(MissingArgumentException.class);
		Resolver.remove(mr);
	}

	@Test
	void testSpecialCaseArgumentInvalid() {
		Resolver<String> mr = new MrResolver();
		context.getArguments().put(HelloMr.class, new ArrayList<>(Arrays.asList("Dr. Johnson")));
		Resolver.register(mr);
		Veracity.assertThat(() -> Invoker.of(HelloMr.class).run(context)).doesThrow(MissingArgumentException.class);
		Resolver.remove(mr);
	}

	@Test
	void testCodeCoverage() { // TODO some of this can be extracted to real tests
		Invoker.of(Hello.class).registerWithParent();
		Invoker.of(Hello.class).unregisterWithParent();
	}

	@Permission("hello.world")
	static class PermissionCheck implements Command {
		@Override
		public void run(Context context) {
		}
	}

	@Usage("/hello <your argument>")
	static class UsageDescribed implements Command {
		@Override
		public void run(Context context) {
		}
	}

	@Description("say hello")
	static class DescriptionDescribed implements Command {
		@Override
		public void run(Context context) {
		}
	}

	@Alias("hi")
	static class Aliased implements Command {
		@Override
		public void run(Context context) {
		}
	}

	@Sync
	static class SyncCommand implements Command {
		@Override
		public void run(Context context) {
		}
	}

	static class Hello implements Command {
		@Override
		public void run(Context context) {
		}
	}

	static class HelloCommand implements Command {
		@Override
		public void run(Context context) {
		}
	}

	@Name("greetings")
	static class HelloGiven implements Command {
		@Override
		public void run(Context context) {
		}
	}

	static class HelloName implements Command {
		static String last;

		@Argument
		String name;

		@Override
		public void run(Context context) {
			last = name;
		}
	}

	static class HelloNameOptional implements Command {
		@Argument(optional = true)
		String name;

		@Override
		public void run(Context context) {
		}
	}

	static class HelloMr implements Command {
		static String last;

		@Argument
		@Mr
		String mr;

		@Override
		public void run(Context context) {
			last = mr;
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@interface Mr {
	}

	static class MrResolver extends Resolver<String> {
		MrResolver() {
			super(String.class);
		}

		@Override
		public String apply(ResolutionRequest request) {
			return request.getArgument().startsWith("Mr.") ? request.getArgument() : null;
		}
		
	}

}