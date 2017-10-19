package com.ulfric.plugin.commands;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ulfric.dragoon.exception.Try;
import com.ulfric.i18n.content.Details;
import com.ulfric.plugin.locale.TellService;

public abstract class Command implements Runnable {

	private static final Map<Class<?>, List<Field>> INSTANCE_FIELDS = new IdentityHashMap<>();

	protected Context context;

	protected final void tell(String text) {
		tell(text, details());
	}

	protected final void tell(String text, Details details) {
		tell(context.getSender(), text, details);
	}

	protected final void tell(CommandSender target, String text) {
		tell(target, text, details());
	}

	protected final void tell(CommandSender target, String text, Details details) {
		TellService.sendMessage(target, text, details);
	}

	protected final UUID uniqueId() {
		return player().getUniqueId();
	}

	protected final Player player() {
		return Context.getPlayer(context);
	}

	public Details details() {
		Details details = Details.none();

		instanceFields().forEach(field -> details.add(field.getName(), readField(field)));

		return details;
	}

	private Object readField(Field field) {
		return Try.toGet(() -> field.get(this));
	}

	private List<Field> instanceFields() {
		return INSTANCE_FIELDS.computeIfAbsent(getClass(), this::instanceFields);
	}

	private List<Field> instanceFields(Class<?> type) {
		return FieldUtils.getAllFieldsList(type)
				.stream()
				.filter(field -> !field.isSynthetic())
				.filter(field -> !Modifier.isStatic(field.getModifiers()))
				.filter(field -> !Modifier.isTransient(field.getModifiers()))
				.peek(field -> field.setAccessible(true))
				.collect(Collectors.toList());
	}

}