package com.ulfric.plugin.commands.argument;

import com.ulfric.commons.value.Bean;
import com.ulfric.plugin.commands.Command;

import java.util.List;
import java.util.Map;

public class Arguments extends Bean {

	private List<String> allArguments;
	private Map<Class<? extends Command>, List<String>> arguments;

	public List<String> getAllArguments() {
		return allArguments;
	}

	public void setAllArguments(List<String> allArguments) {
		this.allArguments = allArguments;
	}

	public Map<Class<? extends Command>, List<String>> getArguments() {
		return arguments;
	}

	public void setArguments(Map<Class<? extends Command>, List<String>> arguments) {
		this.arguments = arguments;
	}

}
