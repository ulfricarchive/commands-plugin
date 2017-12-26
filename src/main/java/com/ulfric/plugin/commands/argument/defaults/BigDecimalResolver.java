package com.ulfric.plugin.commands.argument.defaults;

import java.math.BigDecimal;
import java.util.Optional;

import com.ulfric.commons.math.NumberHelper;
import com.ulfric.plugin.commands.argument.ResolutionRequest;
import com.ulfric.plugin.commands.argument.Resolver;

public class BigDecimalResolver extends Resolver<BigDecimal> {

	public BigDecimalResolver() {
		super(BigDecimal.class);
	}

	@Override
	public BigDecimal apply(ResolutionRequest request) {
		Optional<BigDecimal> value = NumberHelper.parseBigDecimal(request.getArgument());
		return value.orElse(null);
	}

}
