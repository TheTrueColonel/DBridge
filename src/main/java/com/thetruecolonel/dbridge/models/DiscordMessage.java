package com.thetruecolonel.dbridge.models;

import com.github.bsideup.jabel.Desugar;
import net.dv8tion.jda.api.entities.Message;

import java.time.Instant;
import java.util.List;

@Desugar
public record DiscordMessage(long id, int type, String content, String channelName, Instant timestamp, DiscordAuthor author,
                             List<Message.Attachment> attachments) { }
