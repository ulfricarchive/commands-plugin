package com.ulfric.andrew.argument;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

class MatchResolverTest {

	private MatchResolver resolver;
	private ResolutionRequest request;
	private ArgumentDefinition definition;

	/**
	 * 
	 */
	@BeforeEach
	void setup() {
		resolver = new MatchResolver();
		request = new ResolutionRequest();
		definition = new ArgumentDefinition();
		request.setArgument("hello");
		request.setDefinition(definition);
		definition.setName("hello");
		definition.setType(Flag.class);
	}

	@Test
	void testNoMatchAnnotation() throws Exception {
		definition.setField(MatchResolverWillNotTouch.class.getDeclaredField("doNotTouch"));
		Truth.assertThat(resolver.apply(request)).isNull();
	}

	@Test
	void testMatches() throws Exception {
		definition.setField(MatchResolverExample.class.getDeclaredField("flag"));
		Truth.assertThat(resolver.apply(request)).isNotNull();
	}

	@Test
	void testDoesNotMatche() throws Exception {
		definition.setField(MatchResolverExample.class.getDeclaredField("flag"));
		request.setArgument("no match!");
		Truth.assertThat(resolver.apply(request)).isNull();
	}

	static class MatchResolverWillNotTouch {
		Flag doNotTouch;
	}

	static class MatchResolverExample {
		@Match("hello")
		Flag flag;
	}

}