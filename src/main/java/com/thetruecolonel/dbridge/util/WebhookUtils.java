package com.thetruecolonel.dbridge.util;

import com.thetruecolonel.dbridge.DBridge;
import me.micartey.webhookly.DiscordWebhook;
import net.minecraftforge.event.ServerChatEvent;

import java.io.IOException;

public final class WebhookUtils {
    private WebhookUtils() {
        /* This utility class should not be instantiated */
    }

    public static void sendUserMessage(DiscordWebhook webhook, ServerChatEvent event) {
        String headImage = "https://mc-heads.net/avatar/" + event.username + "/100";

        webhook.setUsername(event.username);
        webhook.setAvatarUrl(headImage);
        webhook.setContent(event.message);

        try {
            webhook.execute();
        } catch (IOException ex) {
            DBridge.LOG.error("Failed to send User Message!", ex);
        }
    }

    public static void sendSystemMessage(DiscordWebhook webhook, String message) {
        webhook.setUsername("Server");
        webhook.setAvatarUrl("");
        webhook.setContent(message);

        try {
            webhook.execute();
        } catch (IOException ex) {
            DBridge.LOG.error("Failed to send system message!", ex);
        }
    }
}
