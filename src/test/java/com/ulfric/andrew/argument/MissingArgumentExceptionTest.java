package com.ulfric.andrew.argument;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.veracity.suite.ConstructTestSuite;

@RunWith(JUnitPlatform.class)
class MissingArgumentExceptionTest extends ConstructTestSuite {

	MissingArgumentExceptionTest() {
		super(MissingArgumentException.class);
	}

}