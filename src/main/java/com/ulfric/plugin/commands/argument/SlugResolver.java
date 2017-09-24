package com.ulfric.plugin.commands.argument;

import org.apache.commons.lang3.StringUtils;

import com.ulfric.commons.text.StringHelper;
import com.ulfric.dragoon.stereotype.Stereotypes;

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
