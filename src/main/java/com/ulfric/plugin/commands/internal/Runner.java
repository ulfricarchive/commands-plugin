package com.ulfric.plugin.commands.internal;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.ulfric.i18n.content.Details;
import com.ulfric.plugin.commands.CommandException;
import com.ulfric.plugin.commands.Context;
import com.ulfric.plugin.commands.MissingPermissionException;
import com.ulfric.plugin.commands.MustBePlayerException;
import com.ulfric.plugin.commands.argument.MissingArgumentException;
import com.ulfric.plugin.commands.confirmation.ConfirmationRequiredException;
import com.ulfric.plugin.locale.TellService;

final class Runner implements Consumer<Context> {

	private final CommandRegistry registry;
	private final Executor executor;

	Runner(CommandRegistry registry, Executor executor) {
		this.registry = registry;
		this.executor = executor;
	}

	@Override
	public void accept(Context context) {
		executor.execute(() -> {
			try {
				registry.dispatch(context);
			} catch (Exception thrown) {
				handleError(context, thrown);
			}
		});
	}

	private void handleError(Context context, Exception thrown) {
		Details details = Details.none();
		details.add("context", context);
		details.add("error", thrown);

		String message;

		// TODO cleanup
		try {
			throw thrown;
		} catch (MissingPermissionException permissionCheck) {
			message = permissionCheck.getPermissionMessage();
			if (StringUtils.isEmpty(message)) {
				message = "command-no-permission";
			}
			details.add("node", permissionCheck.getMessage());
		} catch (MissingArgumentException requiredArgument) {
			message = requiredArgument.getArgumentMessage();
			if (StringUtils.isEmpty(message)) {
				message = "command-missing-argument";
			}
			details.add("argument", requiredArgument.getMessage());
		} catch (MustBePlayerException mustBePlayer) {
			message = "command-must-be-player";
		} catch (ConfirmationRequiredException confirmation) {
			message = confirmation.getMessage();
		} catch (CommandException exit) {
			message = exit.getMessage();
		} catch (Exception exception) {
			message = "command-failed-execution";
			exception.printStackTrace(); // TODO error handling
		}

		TellService.sendMessage(context.getSender(), message, details);
	}

}
