package com.ulfric.andrew.argument;

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
		return request.getArgument().matches(match.value()) ? FlagIsPresent.INSTANCE : null; // TODO cache
	}

	enum FlagIsPresent implements Flag {
		INSTANCE;
	}

}
