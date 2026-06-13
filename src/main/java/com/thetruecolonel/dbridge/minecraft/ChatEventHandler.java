package com.thetruecolonel.dbridge.minecraft;

import com.thetruecolonel.dbridge.DBridge;
import com.thetruecolonel.dbridge.models.DiscordMessage;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

import java.io.IOException;
import java.util.Queue;

import me.micartey.webhookly.DiscordWebhook;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.ServerChatEvent;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatEventHandler {
    private final DiscordWebhook webhook;
    private final ConcurrentLinkedQueue<DiscordMessage> inboundQueue;

    public ChatEventHandler(DiscordWebhook webhook, Queue<DiscordMessage> queue) {
        this.webhook = webhook;
        this.inboundQueue = (ConcurrentLinkedQueue<DiscordMessage>) queue;
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        String headImage = "https://mc-heads.net/avatar/" + event.username + "/100";

        webhook.setUsername(event.username);
        webhook.setAvatarUrl(headImage);
        webhook.setContent(event.message);

        try {
            webhook.execute();
        } catch (IOException exception) {
            // Do nothing
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START)
            return;

        DiscordMessage msg;
        while ((msg = inboundQueue.poll()) != null) {
            String outputMessage = new StringBuffer()
                    .append("<#")
                    .append(DBridge.getChannelName())
                    .append(" | ")
                    .append(msg.getAuthor().getUsername())
                    .append("> ")
                    .append(msg.getContent()).toString();

            MinecraftServer.getServer()
                    .getConfigurationManager()
                    .sendChatMsg(new ChatComponentText(outputMessage));
        }
    }
}
