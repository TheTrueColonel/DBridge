package com.thetruecolonel.dbridge.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class DBridgeConfig {
    private final String webhookUrl;
    private final String channelId;
    private final String botToken;

    public DBridgeConfig(File configFile) {
        Configuration config = new Configuration(configFile);

        config.load();

        webhookUrl = config.get(Configuration.CATEGORY_GENERAL, "webhookUrl", "").getString();
        channelId = config.get(Configuration.CATEGORY_GENERAL, "channelId", "").getString();
        botToken = config.get(Configuration.CATEGORY_GENERAL, "botToken", "").getString();

        if (config.hasChanged()) {
            config.save();
        }
    }

    public String getWebhookUrl() { return webhookUrl; }
    public String getChannelId() { return channelId; }
    public String getBotToken() { return botToken; }
}
