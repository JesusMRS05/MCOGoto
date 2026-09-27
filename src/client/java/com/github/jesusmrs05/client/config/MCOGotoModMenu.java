package com.github.jesusmrs05.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.github.jesusmrs05.client.MCOGotoClient;

public final class MCOGotoModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return previousScreen ->
                new MCOGotoConfigScreen(
                        previousScreen,
                        MCOGotoClient.getConfig()
                );
    }
}