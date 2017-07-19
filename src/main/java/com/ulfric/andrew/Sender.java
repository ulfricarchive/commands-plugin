package com.ulfric.andrew;

import com.ulfric.commons.naming.Named;

import java.util.UUID;

public interface Sender extends Named {

	UUID getUniqueId();

	boolean hasPermission(String permission);

	void sendMessage(String message);

}
