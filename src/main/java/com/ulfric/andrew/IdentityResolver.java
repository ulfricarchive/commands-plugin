package com.ulfric.andrew;

public class IdentityResolver extends Resolver<String> {

	public IdentityResolver() {
		super(String.class);
	}

	@Override
	public String apply(ArgumentDefinition definition, String value) {
		System.out.println(1);
		if (definition.getField().getAnnotations().length == 1) {
			return value;
		}
		return null;
	}

}
