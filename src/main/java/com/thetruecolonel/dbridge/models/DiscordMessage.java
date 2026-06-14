package com.thetruecolonel.dbridge.models;

import java.time.Instant;
import java.util.List;

public class DiscordMessage {
    private long id;
    private int type;
    private String content;
    private Instant timestamp;
    private DiscordAuthor author;
    private List<DiscordAttachment> attachments;

    public long getId() { return id; }
    public int getType() { return type; }
    public String getContent() { return content; }
    public Instant getTimestamp() { return timestamp; }
    public DiscordAuthor getAuthor() { return author; }
    public List<DiscordAttachment> getAttachments() { return attachments; }
}
