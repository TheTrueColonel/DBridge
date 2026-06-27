package com.thetruecolonel.dbridge.util;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.minecraftforge.event.ServerChatEvent;

public final class WebhookUtils {
    private WebhookUtils() {
        /* This utility class should not be instantiated */
    }

    /**
     * @see WebhookUtils#fireWebhook(WebhookClient, String, String, String, WebhookEmbed)
     */
    public static void fireWebhook(WebhookClient webhook, String avatarUrl, String content, String username) {
        WebhookUtils.fireWebhook(webhook, avatarUrl, content, username, null);
    }

    /**
     * @see WebhookUtils#fireWebhook(WebhookClient, String, String, String, WebhookEmbed)
     */
    public static void fireWebhook(WebhookClient webhook, String avatarUrl, String username, WebhookEmbed embedObject) {
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
    public static void fireWebhook(WebhookClient webhook, String avatarUrl, String content, String username, WebhookEmbed embed) {
        WebhookMessageBuilder builder = new WebhookMessageBuilder()
            .setUsername(username)
            .setAvatarUrl(avatarUrl)
            .setContent(content)
            .setAllowedMentions(AllowedMentions.none());

        if (embed != null) {
            builder.addEmbeds(embed);
        }

        webhook.send(builder.build());
    }

    public static void sendUserMessage(WebhookClient webhook, ServerChatEvent event) {
        WebhookUtils.fireWebhook(
            webhook,
            PlayerUtils.getAvatarUrl(event.username),
            event.username,
            event.message
        );
    }

    public static void sendSystemMessage(WebhookClient webhook, String message) {
        WebhookUtils.fireWebhook(
            webhook,
            null,
            ServerConstants.SERVER_NAME,
            message
        );
    }
}
