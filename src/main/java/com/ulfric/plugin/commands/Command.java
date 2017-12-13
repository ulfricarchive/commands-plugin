package com.ulfric.plugin.commands;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Throwables;
import com.ulfric.commons.bukkit.command.RunCommandCallback;
import com.ulfric.dragoon.exception.Try;
import com.ulfric.i18n.content.Details;
import com.ulfric.plugin.locale.InputService;
import com.ulfric.plugin.locale.TellService;

public abstract class Command implements Runnable, CommandExtension {

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

	protected final void internalError(String message, Throwable thrown) {
		Objects.requireNonNull(message, "message");
		Objects.requireNonNull(thrown, "thrown");

		Details details = details();
		details.add("error", thrown);
		tell(message, details);

		Throwables.throwIfUnchecked(thrown);
		throw new RuntimeException(message, thrown);
	}

	protected final <T> Optional<T> ifPlayer(Function<Player, T> function) {
		if (sender() instanceof Player) {
			return Optional.ofNullable(function.apply(player()));
		}

		return Optional.empty();
	}

	protected final void requestOnSign(String text, String callback) {
		Player player = player();
		InputService.requestOnSign(player, text,
				new RunCommandCallback(player, callback));
	}

	@Override
	public final UUID uniqueId() {
		return player().getUniqueId();
	}

	@Override
	public final Player player() {
		return Context.getPlayer(context);
	}

	@Override
	public final CommandSender sender() {
		return context.getSender();
	}

	public Details details() {
		Details details = new Details();

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

	public void setContext(Context context) {
		this.context = context;
	}

	public void prerun() {
	}

	public void postrun() {
	}

}
