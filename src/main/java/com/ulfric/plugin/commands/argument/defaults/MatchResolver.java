package com.ulfric.plugin.commands.argument.defaults;

import com.ulfric.commons.text.RegexHelper;
import com.ulfric.dragoon.stereotype.Stereotypes;
import com.ulfric.plugin.commands.argument.Flag;
import com.ulfric.plugin.commands.argument.Match;
import com.ulfric.plugin.commands.argument.ResolutionRequest;
import com.ulfric.plugin.commands.argument.Resolver;

public class MatchResolver extends Resolver<Flag> {

	public MatchResolver() {
		super(Flag.class);
	}

	@Override
	public Flag apply(ResolutionRequest request) {
		Match match = Stereotypes.getFirst(request.getDefinition().getField(), Match.class);
		if (match == null) {
			return null;
		}
		return RegexHelper.matches(request.getArgument(), match.value()) ? FlagIsPresent.INSTANCE : null;
	}

	enum FlagIsPresent implements Flag {
		INSTANCE;
	}

}
