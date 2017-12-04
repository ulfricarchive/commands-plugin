package com.ulfric.plugin.commands.argument.defaults;

import com.ulfric.plugin.commands.Context;
import com.ulfric.plugin.commands.argument.ResolutionRequest;
import com.ulfric.plugin.commands.argument.Resolver;

public class ContextResolver extends Resolver<Context> {

	public ContextResolver() {
		super(Context.class);
	}

	@Override
	public Context apply(ResolutionRequest request) {
		return request.getContext();
	}

}
