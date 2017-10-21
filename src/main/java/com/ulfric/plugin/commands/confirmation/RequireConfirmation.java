package com.ulfric.plugin.commands.confirmation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE })
public @interface RequireConfirmation {

	String message();

	long duration();

	TimeUnit unit();

}
