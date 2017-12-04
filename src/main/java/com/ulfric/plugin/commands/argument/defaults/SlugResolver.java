package com.ulfric.plugin.commands.argument.defaults;

import org.apache.commons.lang3.StringUtils;

import com.ulfric.commons.text.StringHelper;
import com.ulfric.dragoon.stereotype.Stereotypes;
import com.ulfric.plugin.commands.argument.ResolutionRequest;
import com.ulfric.plugin.commands.argument.Resolver;
import com.ulfric.plugin.commands.argument.Slug;

public class SlugResolver extends Resolver<String> {

	public SlugResolver() {
		super(String.class);
	}

	@Override
	public String apply(ResolutionRequest request) {
		if (!Stereotypes.isPresent(request.getDefinition().getField(), Slug.class)) {
			return null;
		}

		String slug = StringHelper.toSlug(request.getArgument());
		return StringUtils.isEmpty(slug) ? null : slug;
	}

}
