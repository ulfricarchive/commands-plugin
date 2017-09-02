package com.ulfric.andrew.argument;

import com.ulfric.commons.reflect.AnnotationHelper;

public class IdentityResolver extends Resolver<String> {

	public IdentityResolver() {
		super(String.class);
	}

	@Override
	public String apply(ResolutionRequest request) {
		if (AnnotationHelper.countDirectAnnotations(request.getDefinition().getField()) == 1) {
			return request.getArgument();
		}
		return null;
	}

}
