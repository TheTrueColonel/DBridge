package com.thetruecolonel.dbridge.minecraft;

import com.thetruecolonel.dbridge.DBridge;
import com.thetruecolonel.dbridge.models.DiscordAttachment;
import com.thetruecolonel.dbridge.models.DiscordMessage;
import com.thetruecolonel.dbridge.util.WebhookUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

import java.util.Queue;

import me.micartey.webhookly.DiscordWebhook;
import net.minecraft.event.ClickEvent;
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
        WebhookUtils.sendUserMessage(webhook, event);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        DiscordMessage msg;
        while ((msg = inboundQueue.poll()) != null) {
            StringBuilder outputMessageBuffer = new StringBuilder()
                    .append("<#")
                    .append(DBridge.getChannelName())
                    .append(" | ")
                    .append(msg.getAuthor().getUsername());

            if (msg.getContent().isEmpty()) {
                outputMessageBuffer.append(">");
            } else {
                outputMessageBuffer.append("> ").append(msg.getContent());
            }

            ChatComponentText component = new ChatComponentText(outputMessageBuffer.toString());

            if (!msg.getAttachments().isEmpty()) {
                for (DiscordAttachment attachment : msg.getAttachments()) {
                    ChatComponentText link = new ChatComponentText(" [Attachment]");

                    link.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, attachment.getUrl()));

                    component.appendSibling(link);
                }
            }

            MinecraftServer.getServer()
                    .getConfigurationManager()
                    .sendChatMsg(component);
        }
    }
}
