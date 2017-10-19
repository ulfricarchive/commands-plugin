package com.ulfric.plugin.commands;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.ulfric.plugin.commands.argument.Arguments;

public abstract class SkeletalRegistry implements Registry {

	@Override
	public void dispatch(Context context) {
		validate(context);

		Invoker command = getCommand(context);
		if (command == null) {
			return;
		}

		dispatch(command, context);
	}

	private Invoker getCommand(Context context) {
		return getCommand(context.getLabels().getRoot());
	}

	private void dispatch(Invoker invoker, Context context) { // TODO cleanup method
		Arguments contextArguments = context.getArguments();
		Map<Class<? extends Command>, List<String>> argumentsByCommand = contextArguments.getArguments();
		if (MapUtils.isEmpty(argumentsByCommand)) {

			List<String> enteredArguments = contextArguments.getAllArguments();
			if (CollectionUtils.isEmpty(enteredArguments)) {
				invoker.run(context);
				return;
			}

			argumentsByCommand = new IdentityHashMap<>();
			argumentsByCommand.put(invoker.getCommand(), new ArrayList<>(enteredArguments));
			contextArguments.setArguments(argumentsByCommand);
		}

		List<String> arguments = argumentsByCommand.get(invoker.getCommand());
		if (CollectionUtils.isEmpty(arguments)) {
			invoker.run(context);
			return;
		}

		for (int x = 0, l = arguments.size(); x < l; x++) {
			String argument = arguments.get(x);
			Invoker child = invoker.getChild(argument);
			if (child != null) {
				if (x != 0) {
					argumentsByCommand.put(invoker.getCommand(), new ArrayList<>(arguments.subList(0, x)));
				}
				argumentsByCommand.put(child.getCommand(), new ArrayList<>(arguments.subList(x + 1, l)));
				dispatch(child, context);
				return;
			}
		}

		invoker.run(context);
	}

	private void validate(Context context) {
		Objects.requireNonNull(context, "context");
		Objects.requireNonNull(context.getLabels(), "context.labels");
		Objects.requireNonNull(context.getLabels().getRoot(), "context.labels.root");
		Objects.requireNonNull(context.getArguments(), "context.arguments");
	}

}