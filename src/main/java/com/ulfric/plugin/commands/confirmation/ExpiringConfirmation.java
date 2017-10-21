package com.ulfric.plugin.commands.confirmation;

import java.util.UUID;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public final class ExpiringConfirmation implements Confirmation {

	private final Cache<UUID, Boolean> cache;

	public ExpiringConfirmation(RequireConfirmation context) {
		this.cache = CacheBuilder.newBuilder()
				.expireAfterWrite(context.duration(), context.unit())
				.concurrencyLevel(1)
				.build();
	}

	@Override
	public boolean test(UUID sender) {
		if (cache.getIfPresent(sender) == null) {
			cache.put(sender, Boolean.TRUE);
			return false;
		}

		cache.invalidate(sender);
		return true;
	}

}
