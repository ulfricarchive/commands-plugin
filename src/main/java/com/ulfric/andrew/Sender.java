package com.ulfric.andrew;

import com.ulfric.commons.naming.Named;
import com.ulfric.i18n.content.Details;

import java.util.UUID;

public interface Sender extends Named {

	UUID getUniqueId();

	boolean hasPermission(String permission);

	void sendMessage(String message);

	void sendMessage(String message, Details details);

	Object handle();

}
