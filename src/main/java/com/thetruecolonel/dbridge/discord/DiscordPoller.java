package com.thetruecolonel.dbridge.discord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thetruecolonel.dbridge.DBridge;
import com.thetruecolonel.dbridge.models.DiscordMessage;
import com.thetruecolonel.dbridge.util.adapters.InstantAdapter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DiscordPoller {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();

    private static final int[] validTypes = { 0, 19 };

    private final String channelId;
    private final String botToken;
    private final OkHttpClient client = new OkHttpClient();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ConcurrentLinkedQueue<DiscordMessage> messageQueue;

    private long lastMessageId = 0;
    private ScheduledFuture<?> task;

    public DiscordPoller(String channelId, String botToken, Queue<DiscordMessage> queue) {
        this.channelId = channelId;
        this.botToken = botToken;
        this.messageQueue = (ConcurrentLinkedQueue<DiscordMessage>) queue;
    }

    public void start() {
        task = scheduler.scheduleWithFixedDelay(this::poll, 0, 2500, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (task != null)
            task.cancel(false);

        scheduler.shutdown();
    }

    private void poll() {
        String url = "https://discord.com/api/v10/channels/"
                + channelId + "/messages?limit=5"
                + (lastMessageId != 0 ? "&after=" + lastMessageId : "");

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bot " + botToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                return;

            ResponseBody body = response.body();

            if (body == null)
                return;

            parseAndQueue(body.string());
        } catch (Exception ex) {
            DBridge.LOG.error("Error polling discord channel: {}, lastMessageId: {}", channelId, lastMessageId, ex);
        }
    }

    private void parseAndQueue(String json) {
        if (json == null || json.isEmpty())
            return;

        DiscordMessage[] parsedMessages = GSON.fromJson(json, DiscordMessage[].class);

        if (parsedMessages.length == 0)
            return;

        Arrays.sort(parsedMessages, Comparator.comparing(DiscordMessage::getTimestamp));

        List<DiscordMessage> toQueue = Arrays.stream(parsedMessages)
                .filter(discordMessage -> !discordMessage.getAuthor().isBot() &&
                        IntStream.of(validTypes).anyMatch(x -> x == discordMessage.getType()))
                .collect(Collectors.toList());

        if (lastMessageId == 0 || toQueue.isEmpty()) {
            lastMessageId = parsedMessages[parsedMessages.length - 1].getId();

            return;
        }

        for (DiscordMessage message : toQueue) {
            lastMessageId = message.getId();

            messageQueue.add(message);
        }
    }
}
