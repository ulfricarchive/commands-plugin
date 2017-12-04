package com.ulfric.plugin.commands.permissions;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;

import com.ulfric.dragoon.stereotype.Stereotypes;
import com.ulfric.plugin.commands.CommandPreRunEvent;
import com.ulfric.plugin.commands.Stage;

public class PermissionVerificationStage extends Stage<List<Permission>> {

	@EventHandler(ignoreCancelled = true)
	public void on(CommandPreRunEvent event) {
		List<Permission> permissions = commandToContext.computeIfAbsent(event.getContext().getCommandType(),
				command -> Stereotypes.getAll(command, Permission.class));

		CommandSender sender = event.getContext().getSender();
		for (Permission permission : permissions) {
			if (!sender.hasPermission(permission.value())) {
				event.cancel(
						new MissingPermissionException(event.getContext(), permission.message(), permission.value()));
				return;
			}
		}
	}

}
