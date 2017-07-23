package com.ulfric.andrew.argument;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.veracity.suite.EnumTestSuite;

@RunWith(JUnitPlatform.class)
class MatchResolverFlagIsPresentTest extends EnumTestSuite { // TODO move into MatchResolverTest

	public MatchResolverFlagIsPresentTest() {
		super(MatchResolver.FlagIsPresent.class);
	}

}