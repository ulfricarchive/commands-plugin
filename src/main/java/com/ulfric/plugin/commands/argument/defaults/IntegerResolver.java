package com.ulfric.plugin.commands.argument.defaults;

import com.ulfric.commons.math.NumberHelper;
import com.ulfric.plugin.commands.argument.ResolutionRequest;
import com.ulfric.plugin.commands.argument.Resolver;

import java.util.OptionalInt;

public class IntegerResolver extends Resolver<Integer> {

	public IntegerResolver() {
		super(Integer.class, int.class);
	}

	@Override
	public Integer apply(ResolutionRequest request) {
		OptionalInt value = NumberHelper.parseInt(request.getArgument());
		return value.isPresent() ? value.getAsInt() : null;
	}

}
