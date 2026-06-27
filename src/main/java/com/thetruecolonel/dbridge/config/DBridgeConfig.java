package com.thetruecolonel.dbridge.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class DBridgeConfig {
    private final String webhookId;
    private final String channelId;
    private final String botToken;

    public DBridgeConfig(File configFile) {
        Configuration config = new Configuration(configFile);

        config.load();

        webhookId = config.get(Configuration.CATEGORY_GENERAL, "webhookId", "").getString();
        channelId = config.get(Configuration.CATEGORY_GENERAL, "channelId", "").getString();
        botToken = config.get(Configuration.CATEGORY_GENERAL, "botToken", "").getString();

        if (config.hasChanged()) {
            config.save();
        }
    }

    public String getWebhookId() { return webhookId; }
    public String getChannelId() { return channelId; }
    public String getBotToken() { return botToken; }
}
