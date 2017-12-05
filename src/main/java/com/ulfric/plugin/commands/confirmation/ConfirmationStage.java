package com.ulfric.plugin.commands.confirmation;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;

import com.ulfric.commons.bukkit.command.CommandSenderHelper;
import com.ulfric.dragoon.stereotype.Stereotypes;
import com.ulfric.plugin.commands.CommandPreRunEvent;
import com.ulfric.plugin.commands.Stage;
import com.ulfric.plugin.commands.confirmation.ConfirmationStage.ConfirmationWithMessage;

public class ConfirmationStage extends Stage<ConfirmationWithMessage> {

	@EventHandler(ignoreCancelled = true)
	public void on(CommandPreRunEvent event) {
		ConfirmationWithMessage confirmation = commandToContext.computeIfAbsent(event.getCommandType(), command -> {
			RequireConfirmation context = Stereotypes.getFirst(command, RequireConfirmation.class);

			ConfirmationWithMessage confirmationWithMessage = new ConfirmationWithMessage();

			if (context == null) {
				confirmationWithMessage.confirmation = ConfirmationNotRequired.INSTANCE;
			} else {
				confirmationWithMessage.confirmation = new ExpiringConfirmation(context);
				confirmationWithMessage.message = context.message();
			}

			return confirmationWithMessage;
		});

		CommandSender sender = event.getContext().getSender();
		UUID uniqueId = CommandSenderHelper.getUniqueId(sender);
		if (uniqueId == null) {
			return;
		}

		if (!confirmation.confirmation.test(uniqueId)) {
			event.cancel(new ConfirmationRequiredException(event.getContext(), confirmation.message));
		}
	}

	static final class ConfirmationWithMessage {
		String message;
		Confirmation confirmation;
	}

}
