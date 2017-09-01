package com.ulfric.andrew.argument;

import com.ulfric.commons.text.RegexHelper;

public class MatchResolver extends Resolver<Flag> {

	public MatchResolver() {
		super(Flag.class);
	}

	@Override
	public Flag apply(ResolutionRequest request) {
		Match match = request.getDefinition().getField().getAnnotation(Match.class);
		if (match == null) {
			return null;
		}
		return RegexHelper.matches(request.getArgument(), match.value()) ? FlagIsPresent.INSTANCE : null;
	}

	enum FlagIsPresent implements Flag {
		INSTANCE;
	}

}
