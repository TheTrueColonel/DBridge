package com.thetruecolonel.dbridge;

import com.google.gson.Gson;
import com.thetruecolonel.dbridge.config.DBridgeConfig;
import com.thetruecolonel.dbridge.discord.DiscordPoller;
import com.thetruecolonel.dbridge.minecraft.ChatEventHandler;
import com.thetruecolonel.dbridge.minecraft.CommandEventHandler;
import com.thetruecolonel.dbridge.minecraft.DeathEventHandler;
import com.thetruecolonel.dbridge.minecraft.JoinLeaveEventHandler;
import com.thetruecolonel.dbridge.models.DiscordChannel;
import com.thetruecolonel.dbridge.models.DiscordMessage;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import me.micartey.webhookly.DiscordWebhook;
import net.minecraftforge.common.MinecraftForge;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;

@Mod(modid = DBridge.MODID, version = DBridge.VERSION, name = DBridge.NAME, acceptableRemoteVersions = "*")
public class DBridge {
    public static final String MODID = "ttcdbridge";
    public static final String VERSION = "1.0.3";
    public static final String NAME = "Discord Bridge";
    public static final Logger LOG = LogManager.getLogger(MODID);

    private static String channelName;

    private DBridgeConfig config;
    private DiscordPoller poller;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.config = new DBridgeConfig(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        ConcurrentLinkedQueue<DiscordMessage> inboundQueue = new ConcurrentLinkedQueue<>();

        DiscordWebhook webhook = new DiscordWebhook(config.getWebhookUrl());
        ChatEventHandler chatHandler = new ChatEventHandler(webhook, inboundQueue);
        CommandEventHandler commandHandler = new CommandEventHandler(webhook);

        JoinLeaveEventHandler joinLeaveHandler = new JoinLeaveEventHandler(webhook);
        DeathEventHandler deathEventHandler = new DeathEventHandler(webhook);

        poller = new DiscordPoller(config.getChannelId(), config.getBotToken(), inboundQueue);

        getChannelName(config);

        MinecraftForge.EVENT_BUS.register(chatHandler);
        MinecraftForge.EVENT_BUS.register(commandHandler);

        FMLCommonHandler.instance().bus().register(chatHandler);
        FMLCommonHandler.instance().bus().register(commandHandler);

        this.registerEventHandlers(
                joinLeaveHandler,
                deathEventHandler
        );

        poller.start();
    }

    private void registerEventHandlers(Object... objs) {
        for (Object o : objs) {
            MinecraftForge.EVENT_BUS.register(o);
            FMLCommonHandler.instance().bus().register(o);
        }
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        poller.stop();
    }

    public static String getChannelName() {
        return channelName;
    }

    private static void getChannelName(DBridgeConfig config) {
        final Gson gson = new Gson();
        final OkHttpClient client = new OkHttpClient();

        String url = "https://discord.com/api/v10/channels/" + config.getChannelId();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bot " + config.getBotToken())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                return;

            ResponseBody body = response.body();

            if (body == null)
                return;

            DiscordChannel channel = gson.fromJson(body.string(), DiscordChannel.class);

            channelName = channel.getName();
        } catch (Exception ex) {
            LOG.error("Unable to get channel name for channelId {}", config.getChannelId(), ex);
        }
    }
}
