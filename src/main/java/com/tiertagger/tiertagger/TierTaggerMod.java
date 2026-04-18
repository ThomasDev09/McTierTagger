package com.tiertagger.tiertagger;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TierTaggerMod implements ClientModInitializer {

    public static final String MOD_ID = "tiertagger";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final int REFRESH_TICKS = 1200;
    private int tickCounter = 0;

    @Override
    public void onInitializeClient() {
        TierConfig.load();
        TierCache.refresh();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            tickCounter++;
            if (tickCounter >= REFRESH_TICKS) {
                tickCounter = 0;
                TierCache.refresh();
            }
        });

        LOGGER.info("TierTagger loaded. Tags will appear above player heads.");
    }
}
