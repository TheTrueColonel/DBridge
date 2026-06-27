package com.thetruecolonel.dbridge.jda;

import com.thetruecolonel.dbridge.config.DBridgeConfig;
import com.thetruecolonel.dbridge.models.DiscordAuthor;
import com.thetruecolonel.dbridge.models.DiscordMessage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.EnumSet;
import java.util.Queue;

public class JdaEvents extends ListenerAdapter {
    private static final EnumSet<MessageType> MESSAGE_TYPES = EnumSet.of(MessageType.DEFAULT, MessageType.INLINE_REPLY);

    private final DBridgeConfig config;
    private final Queue<DiscordMessage> messageQueue;

    public JdaEvents(DBridgeConfig config, Queue<DiscordMessage> queue) {
        this.config = config;
        this.messageQueue = queue;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        Message message = e.getMessage();

        if (e.getAuthor().isBot() || !MESSAGE_TYPES.contains(message.getType())) {
            return;
        }

        if (e.getChannel().getId().equals(config.getChannelId())) {
            DiscordAuthor discordAuthor = new DiscordAuthor(
                e.getAuthor().getName(),
                e.getAuthor().isBot()
            );

            DiscordMessage discordMessage = new DiscordMessage(
                e.getMessageIdLong(),
                message.getType().getId(),
                message.getContentRaw(),
                message.getGuildChannel().getName(),
                message.getTimeCreated().toInstant(),
                discordAuthor,
                e.getMessage().getAttachments()
            );

            messageQueue.add(discordMessage);
        }
    }
}
