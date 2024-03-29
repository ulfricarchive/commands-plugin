package com.ulfric.plugin.commands.argument;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;

import com.ulfric.commons.value.Bean;

public class ArgumentDefinition extends Bean { // TODO immutable

	private String name;
	private String message;
	private Type type;
	private Field field;
	private Boolean optional;
	private Executor executor;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Boolean getOptional() {
		return optional;
	}

	public void setOptional(Boolean optional) {
		this.optional = optional;
	}

	public Executor getExecutor() {
		return executor;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

}
