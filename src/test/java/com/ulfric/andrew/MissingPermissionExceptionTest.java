package com.ulfric.andrew;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.veracity.suite.ConstructTestSuite;

@RunWith(JUnitPlatform.class)
class MissingPermissionExceptionTest extends ConstructTestSuite {

	MissingPermissionExceptionTest() {
		super(MissingPermissionException.class);
	}

}