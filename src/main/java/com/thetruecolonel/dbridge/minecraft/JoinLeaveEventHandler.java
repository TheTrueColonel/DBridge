package com.thetruecolonel.dbridge.minecraft;

import com.thetruecolonel.dbridge.util.PlayerUtils;
import com.thetruecolonel.dbridge.util.ServerConstants;
import com.thetruecolonel.dbridge.util.WebhookUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import me.micartey.webhookly.DiscordWebhook;
import me.micartey.webhookly.embeds.EmbedObject;

import java.awt.Color;

public class JoinLeaveEventHandler {
    private final DiscordWebhook webhook;

    public JoinLeaveEventHandler(DiscordWebhook webhook) {
        this.webhook = webhook;
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        this.handlePlayerEvent(ConnectionState.LOGIN, event);
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        this.handlePlayerEvent(ConnectionState.LOGOUT, event);
    }

    /**
     * Handles a player log-in or log-out event
     * @param state the connection state for embed message
     * @param event the {@link PlayerEvent} that should be handled
     */
    private void handlePlayerEvent(ConnectionState state, PlayerEvent event) {
        // trusting that this is either a log-in or log-out event...
        String username = event.player.getDisplayName();

        WebhookUtils.fireWebhook(
            webhook,
            PlayerUtils.getAvatarUrl(username),
            "",
            ServerConstants.SERVER_EVENT_NAME,
            this.buildEmbedFor(state, username)
        );
    }

    private EmbedObject buildEmbedFor(ConnectionState state, String username) {
        // note: you can set a thumbnail but it kind of looks big
        return new EmbedObject()
                .setColor(state.getEmbedColor())
                .setDescription(String.format("**%s has %s the server**", username, state));
    }

    private enum ConnectionState {
        LOGIN("logged in to", Color.GREEN),
        LOGOUT("logged out of", Color.RED);

        private final String message;

        private final Color embedColor;

        ConnectionState(String message, Color embedColor) {
            this.message = message;
            this.embedColor = embedColor;
        }

        @Override
        public String toString() {
            return this.message;
        }

        public Color getEmbedColor() {
            return embedColor;
        }
    }
}
