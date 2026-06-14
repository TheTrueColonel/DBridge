package com.thetruecolonel.dbridge.util;

import me.micartey.webhookly.DiscordWebhook;
import me.micartey.webhookly.embeds.EmbedObject;
import com.thetruecolonel.dbridge.DBridge;
import net.minecraftforge.event.ServerChatEvent;

import java.io.IOException;

public final class WebhookUtils {
  
    private WebhookUtils() {
        /* This utility class should not be instantiated */
    }

    /**
     * @see WebhookUtils#fireWebhook(DiscordWebhook, String, String, String, EmbedObject)
     */
    public static void fireWebhook(DiscordWebhook webhook, String avatarUrl, String username, String content) {
        WebhookUtils.fireWebhook(webhook, avatarUrl, content, username, null);
    }

    /**
     * @see WebhookUtils#fireWebhook(DiscordWebhook, String, String, String, EmbedObject)
     */
    public static void fireWebhook(DiscordWebhook webhook, String avatarUrl, String username, EmbedObject embedObject) {
        WebhookUtils.fireWebhook(webhook, avatarUrl, "", username, embedObject);
    }

    /**
     * Fires a webhook message
     * @param avatarUrl the avatar url for the webhook
     * @param content the content
     * @param username the username for the webhook
     * @param embed the embed, can be {@code null}
     * @apiNote this method clears the webhook state before and after execution
     * to avoid state contamination.
     */
    public static void fireWebhook(DiscordWebhook webhook, String avatarUrl, String content, String username, EmbedObject embed) {
        try {
            WebhookUtils.cleanWebhookStates(webhook);
            webhook.setAvatarUrl(avatarUrl);
            webhook.setContent(content);
            webhook.setUsername(username);
            webhook.getEmbeds().clear();

            if (embed != null) {
                webhook.getEmbeds().add(embed);
            }

            webhook.execute();
        } catch (IOException ex) {
            DBridge.LOG.error("Failed to send webhook!", ex);
        } finally {
            // clean up
            WebhookUtils.cleanWebhookStates(webhook);
        }
    }

    private static void cleanWebhookStates(DiscordWebhook webhook) {
        // rain: default looks to be null
        webhook.setAvatarUrl(null);
        webhook.setUsername(null);
        webhook.setContent(null);
        webhook.getEmbeds().clear();    
    }
  
    public static void sendUserMessage(DiscordWebhook webhook, ServerChatEvent event) {
        WebhookUtils.fireWebhook(
            webhook,
            PlayerUtils.getAvatarUrl(event.username),
            event.username,
            event.message
        );
    }

    public static void sendSystemMessage(DiscordWebhook webhook, String message) {
        WebhookUtils.fireWebhook(
            webhook,
            null,
            ServerConstants.SERVER_NAME,
            message
        );
    }
}
