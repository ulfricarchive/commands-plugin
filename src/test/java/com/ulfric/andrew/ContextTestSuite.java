package com.ulfric.andrew;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.IdentityHashMap;

public abstract class ContextTestSuite {

	protected Context context;

	@BeforeEach
	final void setupContext() {
		context = new Context();
		context.setLabels(new Labels());
		context.getLabels().setRoot("hello");
		context.setArguments(new Arguments());
		context.getArguments().setAllArguments(new ArrayList<>());
		context.getArguments().setArguments(new IdentityHashMap<>());
		context.setSender(Mockito.mock(CommandSender.class));
	}

}