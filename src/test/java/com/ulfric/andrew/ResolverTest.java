package com.ulfric.andrew;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.google.common.truth.Truth;

@RunWith(JUnitPlatform.class)
class ResolverTest {

	@Test
	void testResolveButResolverMissing() {
		ArgumentDefinition definition = new ArgumentDefinition();
		definition.setType(IllegalType.class);
		Truth.assertThat(Resolver.resolve(definition, "anything")).isNull();
	}

	@Test
	void testCodeCoverage() { // TODO extract these if possible
		Resolver.remove(new Resolver<Object>(Object.class) {
			@Override
			public Object apply(ArgumentDefinition definition, String argument) {
				return null;
			}
		});
	}

	static class IllegalType {
	}

}