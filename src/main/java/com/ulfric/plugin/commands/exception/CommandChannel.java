package com.ulfric.plugin.commands.exception;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.ulfric.dragoon.stereotype.Stereotype;
import com.ulfric.plugin.broken.Channel;

@Retention(RUNTIME)
@Target(FIELD)
@Stereotype
@Channel("command")
public @interface CommandChannel {

}
