package com.ulfric.andrew.argument;

public class IdentityResolver extends Resolver<String> {

	public IdentityResolver() {
		super(String.class);
	}

	@Override
	public String apply(ResolutionRequest request) {
		if (request.getDefinition().getField().getAnnotations().length == 1) {
			return request.getArgument();
		}
		return null;
	}

}
