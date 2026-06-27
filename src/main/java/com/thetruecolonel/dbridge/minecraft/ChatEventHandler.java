package com.thetruecolonel.dbridge.minecraft;

import club.minnced.discord.webhook.WebhookClient;
import com.thetruecolonel.dbridge.models.DiscordMessage;
import com.thetruecolonel.dbridge.util.WebhookUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

import java.util.Queue;

import net.dv8tion.jda.api.entities.Message;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.ServerChatEvent;

public class ChatEventHandler {
    private final WebhookClient webhook;
    private final Queue<DiscordMessage> inboundQueue;

    public ChatEventHandler(WebhookClient webhook, Queue<DiscordMessage> queue) {
        this.webhook = webhook;
        this.inboundQueue = queue;
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
                    .append(msg.channelName())
                    .append(" | ")
                    .append(msg.author().username());

            if (msg.content().isEmpty()) {
                outputMessageBuffer.append(">");
            } else {
                outputMessageBuffer.append("> ").append(msg.content());
            }

            ChatComponentText component = new ChatComponentText(outputMessageBuffer.toString());

            if (!msg.attachments().isEmpty()) {
                for (Message.Attachment attachment : msg.attachments()) {
                    ChatComponentText link = new ChatComponentText(" [Attachment]");

                    link.getChatStyle()
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, attachment.getUrl()))
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Open in browser")));

                    component.appendSibling(link);
                }
            }

            MinecraftServer.getServer()
                    .getConfigurationManager()
                    .sendChatMsg(component);
        }
    }
}
