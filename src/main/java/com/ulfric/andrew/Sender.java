package com.ulfric.andrew;

import com.ulfric.commons.naming.Named;

import java.util.Map;
import java.util.UUID;

public interface Sender extends Named {

	UUID getUniqueId();

	boolean hasPermission(String permission);

	void sendMessage(String message);

	void sendMessage(String message, Map<String, String> context);

	Object handle();

}
