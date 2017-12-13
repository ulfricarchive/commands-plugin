package com.ulfric.plugin.commands.function.defaults;

import com.ulfric.i18n.function.Function;
import com.ulfric.plugin.commands.Context;

public class ContextToCommandSenderFunction extends Function<Context> {

	public ContextToCommandSenderFunction() {
		super("sender", Context.class);
	}

	@Override
	public Object apply(Context context) {
		return context.getSender();
	}

}
