package com.ulfric.plugin.commands.argument;

import java.util.List;

import org.apache.commons.collections4.OrderedMap;

import com.ulfric.plugin.commands.Command;

public class Arguments {

	private List<String> entered;
	private OrderedMap<Class<? extends Command>, EnteredSyntax> arguments;

	public List<String> getEntered() {
		return entered;
	}

	public void setEntered(List<String> entered) {
		this.entered = entered;
	}

	public OrderedMap<Class<? extends Command>, EnteredSyntax> getArguments() {
		return arguments;
	}

	public void setArguments(OrderedMap<Class<? extends Command>, EnteredSyntax> arguments) {
		this.arguments = arguments;
	}

}
