package com.github.jesusmrs05.client;

import com.github.jesusmrs05.client.config.MCOGotoConfig;
import net.fabricmc.api.ClientModInitializer;

public final class MCOGotoClient
		implements ClientModInitializer {

	private static MCOGotoConfig config;

	@Override
	public void onInitializeClient() {
		config = MCOGotoConfig.load();
	}

	public static MCOGotoConfig getConfig() {
		return config;
	}
}