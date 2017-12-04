package com.ulfric.plugin.commands.argument.defaults;

import com.ulfric.commons.reflect.AnnotationHelper;
import com.ulfric.plugin.commands.argument.ResolutionRequest;
import com.ulfric.plugin.commands.argument.Resolver;

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
