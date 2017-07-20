package com.ulfric.andrew;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class SkeletalRegistry implements Registry {

	@Override
	public void dispatch(Context context) {
		validate(context);

		Command command = getCommand(context.getLabel());
		if (command == null) {
			return;
		}

		if (command instanceof Invoker) {
			Invoker invoker = (Invoker) command;
			context.getArguments().put(invoker.getCommand(), context.getArguments().get(Command.class));
			dispatch((Invoker) command, context);
			return;
		}

		command.run(context);
	}

	private void dispatch(Invoker invoker, Context context) {
		Map<Class<? extends Command>, List<String>> contextArguments = context.getArguments();
		List<String> arguments = contextArguments.get(invoker.getCommand());
		if (CollectionUtils.isEmpty(arguments)) {
			invoker.run(context);
			return;
		}

		for (int x = 0, l = arguments.size(); x < l; x++) {
			String argument = arguments.get(x);
			Invoker child = invoker.getChild(argument);
			if (child != null) {
				if (x != 0) {
					contextArguments.put(invoker.getCommand(), arguments.subList(0, x));
				}
				contextArguments.put(child.getCommand(), arguments.subList(x + 1, l));
				dispatch(child, context);
				return;
			}
		}

		invoker.run(context);
	}

	private void validate(Context context) {
		Objects.requireNonNull(context, "context");
		Objects.requireNonNull(context.getLabel(), "context.label");
		Objects.requireNonNull(context.getArguments(), "context.arguments");
	}

}