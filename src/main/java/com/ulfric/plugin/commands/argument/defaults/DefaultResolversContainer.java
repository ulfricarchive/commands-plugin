package com.ulfric.plugin.commands.argument.defaults;

import com.ulfric.dragoon.application.Container;

public class DefaultResolversContainer extends Container {

	public DefaultResolversContainer() {
		install(PlayerResolver.class);
		install(WorldResolver.class);
		install(SlugResolver.class);
		install(MatchResolver.class);
		install(IntegerResolver.class);
		install(ContextResolver.class);
		install(BigDecimalResolver.class);
	}

}