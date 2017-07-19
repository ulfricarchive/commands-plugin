package com.ulfric.andrew;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.veracity.suite.BeanTestSuite;

@RunWith(JUnitPlatform.class)
class ContextTest extends BeanTestSuite<Context> {

	ContextTest() {
		super(Context.class);
	}

}