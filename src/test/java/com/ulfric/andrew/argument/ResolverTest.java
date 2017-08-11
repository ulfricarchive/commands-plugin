package com.ulfric.andrew.argument;

import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

class ResolverTest {

	@Test
	void testResolveButResolverMissing() {
		ArgumentDefinition definition = new ArgumentDefinition();
		definition.setType(IllegalType.class);
		ResolutionRequest request = new ResolutionRequest();
		request.setDefinition(definition);
		request.setArgument("anything");
		Truth.assertThat(Resolver.resolve(request)).isNull();
	}

	@Test
	void testCodeCoverage() { // TODO extract these if possible
		Resolver.remove(new Resolver<Object>(Object.class) {
			@Override
			public Object apply(ResolutionRequest request) {
				return null;
			}
		});
	}

	static class IllegalType {
	}

}