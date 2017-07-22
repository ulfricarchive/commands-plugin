package com.ulfric.andrew.argument;

import com.ulfric.andrew.Context;
import com.ulfric.commons.value.Bean;

public class ResolutionRequest extends Bean {

	private ArgumentDefinition definition;
	private Context context;
	private String argument;

	public ArgumentDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(ArgumentDefinition definition) {
		this.definition = definition;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String getArgument() {
		return argument;
	}

	public void setArgument(String argument) {
		this.argument = argument;
	}

}