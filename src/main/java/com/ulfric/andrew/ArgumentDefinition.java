package com.ulfric.andrew;

import com.ulfric.commons.value.Bean;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class ArgumentDefinition extends Bean { // TODO stop mutability

	private String name;
	private Type type;
	private Field field;
	private Boolean optional;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

}
