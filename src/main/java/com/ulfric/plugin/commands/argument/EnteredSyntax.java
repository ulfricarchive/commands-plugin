package com.ulfric.plugin.commands.argument;

import java.util.List;

import com.ulfric.commons.value.Bean;

public class EnteredSyntax extends Bean {

	private String label;
	private List<String> arguments;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}

}
