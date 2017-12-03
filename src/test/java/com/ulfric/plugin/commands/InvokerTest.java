package com.ulfric.plugin.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.google.common.truth.Truth;
import com.ulfric.commons.naming.Name;
import com.ulfric.plugin.Plugin;
import com.ulfric.plugin.commands.argument.Argument;
import com.ulfric.plugin.commands.argument.MissingArgumentException;
import com.ulfric.plugin.commands.argument.ResolutionRequest;
import com.ulfric.plugin.commands.argument.Resolver;
import com.ulfric.plugin.tasks.Scheduler;
import com.ulfric.veracity.Veracity;

class InvokerTest extends ContextTestSuite {

	private Server mockServer;

	@BeforeEach
	void setup() { // TODO remove this ugly solution
		mockServer = Mockito.mock(Server.class);
		Mockito.doReturn(true).when(mockServer).isPrimaryThread();
		Plugin.getStandardFactory().bind(Server.class).toValue(mockServer);
		Plugin.getStandardFactory().bind(Scheduler.class).toValue(new Scheduler(Mockito.mock(Plugin.class)));
	}

	@AfterEach
	void teardown() {
		Plugin.getStandardFactory().bind(Server.class).toNothing();
		Plugin.getStandardFactory().bind(Scheduler.class).toNothing();
	}

	@Test
	void testOfInvoker() {
		Veracity.assertThat(() -> Invoker.of(Command.class)).doesThrow(IllegalArgumentException.class);
	}

	@Test
	void testOfPermissionCheckFails() {
		Mockito.when(context.getSender().hasPermission(ArgumentMatchers.anyString())).thenReturn(false);
		Veracity.assertThat(() -> Invoker.of(PermissionCheck.class).run(context))
			.doesThrow(MissingPermissionException.class);
	}

	@Test
	void testOfPermissionCheck() {
		Mockito.when(context.getSender().hasPermission(ArgumentMatchers.anyString())).thenReturn(true);
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
		context.getArguments().getArguments().put(HelloName.class, new ArrayList<>(Arrays.asList(name)));
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
		context.getArguments().getArguments().put(HelloNameOptional.class, new ArrayList<>());
		Veracity.assertThat(() -> Invoker.of(HelloNameOptional.class).run(context)).runsWithoutExceptions();
	}

	@Test
	void testSpecialCaseArgument() {
		Resolver<String> mr = new MrResolver();
		context.getArguments().getArguments().put(HelloMr.class, new ArrayList<>(Arrays.asList("Mr. Johnson")));
		Resolver.register(mr);
		Invoker.of(HelloMr.class).run(context);
		Truth.assertThat(HelloMr.last).isEqualTo("Mr. Johnson");
		Resolver.remove(mr);
	}

	@Test
	void testSpecialCaseArgumentMissing() {
		Resolver<String> mr = new MrResolver();
		context.getArguments().getArguments().put(HelloMr.class, new ArrayList<>());
		Resolver.register(mr);
		Veracity.assertThat(() -> Invoker.of(HelloMr.class).run(context)).doesThrow(MissingArgumentException.class);
		Resolver.remove(mr);
	}

	@Test
	void testSpecialCaseArgumentInvalid() {
		Resolver<String> mr = new MrResolver();
		context.getArguments().getArguments().put(HelloMr.class, new ArrayList<>(Arrays.asList("Dr. Johnson")));
		Resolver.register(mr);
		Veracity.assertThat(() -> Invoker.of(HelloMr.class).run(context)).doesThrow(MissingArgumentException.class);
		Resolver.remove(mr);
	}

	@Test
	void testOfAbstract() {
		Veracity.assertThat(() -> Invoker.of(AbstractParent.class)).doesThrow(IllegalArgumentException.class);
	}

	@Test
	void testChildOfAbstract() {
		Invoker parent = Invoker.of(AbstractParentConcreteBase.class);
		Invoker child = Invoker.of(ChildOfAbstractParent.class);
		child.registerWithParent();
		Truth.assertThat(parent.getChild(child.getName())).isSameAs(child);
		child.unregisterWithParent();
	}

	@Test
	void testCodeCoverage() { // TODO some of this can be extracted to real tests
		Invoker.of(Hello.class).registerWithParent();
		Invoker.of(Hello.class).unregisterWithParent();
	}

	@Permission("hello.world")
	static class PermissionCheck extends Command {
		@Override
		public void run() {
		}
	}

	@Usage("/hello <your argument>")
	static class UsageDescribed extends Command {
		@Override
		public void run() {
		}
	}

	@Description("say hello")
	static class DescriptionDescribed extends Command {
		@Override
		public void run() {
		}
	}

	@Alias("hi")
	static class Aliased extends Command {
		@Override
		public void run() {
		}
	}

	static class Hello extends Command {
		@Override
		public void run() {
		}
	}

	static class HelloCommand extends Command {
		@Override
		public void run() {
		}
	}

	@Name("greetings")
	static class HelloGiven extends Command {
		@Override
		public void run() {
		}
	}

	static class HelloName extends Command {
		static String last;

		@Argument
		String name;

		@Override
		public void run() {
			last = name;
		}
	}

	static class HelloNameOptional extends Command {
		@Argument(optional = true)
		String name;

		@Override
		public void run() {
		}
	}

	static class HelloMr extends Command {
		static String last;

		@Argument
		@Mr
		String mr;

		@Override
		public void run() {
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

	@Name("concrete")
	static class AbstractParentConcreteBase extends Command {
		@Override
		public void run() {
		}
	}

	abstract static class AbstractParent extends AbstractParentConcreteBase {
		
	}

	@Name("child")
	static class ChildOfAbstractParent extends AbstractParent {
		@Override
		public void run() {
		}
	}

}