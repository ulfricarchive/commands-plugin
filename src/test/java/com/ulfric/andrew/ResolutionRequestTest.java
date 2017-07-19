package com.ulfric.andrew;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.veracity.suite.BeanTestSuite;

@RunWith(JUnitPlatform.class)
class ResolutionRequestTest extends BeanTestSuite<ResolutionRequest> {

	ResolutionRequestTest() {
		super(ResolutionRequest.class);
	}

}