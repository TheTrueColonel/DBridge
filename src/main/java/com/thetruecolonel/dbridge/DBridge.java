package com.thetruecolonel.dbridge;

import club.minnced.discord.webhook.WebhookClient;
import com.thetruecolonel.dbridge.config.DBridgeConfig;
import com.thetruecolonel.dbridge.jda.JdaEvents;
import com.thetruecolonel.dbridge.minecraft.ChatEventHandler;
import com.thetruecolonel.dbridge.minecraft.CommandEventHandler;
import com.thetruecolonel.dbridge.minecraft.DeathEventHandler;
import com.thetruecolonel.dbridge.minecraft.JoinLeaveEventHandler;
import com.thetruecolonel.dbridge.models.DiscordMessage;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mod(modid = DBridge.MODID, version = Tags.VERSION, name = "Discord Bridge",
    acceptedMinecraftVersions = "[1.7.10]", acceptableRemoteVersions = "*")
public class DBridge {
    public static final String MODID = "ttcdbridge";
    public static final Logger LOG = LogManager.getLogger(MODID);

    private static final EnumSet<GatewayIntent> intents = EnumSet.of(
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.MESSAGE_CONTENT
    );

    private JDA jda;
    private WebhookClient webhook;
    private DBridgeConfig config;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.config = new DBridgeConfig(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        String webhookUrl;
        ConcurrentLinkedQueue<DiscordMessage> inboundQueue = new ConcurrentLinkedQueue<>();

        if (config.getWebhookId().isEmpty() || config.getChannelId().isEmpty() || config.getBotToken().isEmpty()) {
            LOG.error("Config not filled out. Disabling...");

            return;
        }

        try {
            jda = JDABuilder.createLight(config.getBotToken(), intents)
                .addEventListeners(new JdaEvents(config, inboundQueue))
                .setActivity(Activity.playing("I'm gregging it"))
                .build();

            jda.getRestPing().queue(x -> LOG.info("Logged in with ping: {}", x));

            jda.awaitReady();

            TextChannel channel = jda.getTextChannelById(config.getChannelId());

            if (channel == null) {
                LOG.error("Could not find channel with id '{}'. Disabling...", config.getChannelId());

                return;
            }

            List<Webhook> webhooks = channel.retrieveWebhooks().complete();

            Webhook existing = webhooks.stream()
                .filter(x -> x.getName().equals(config.getWebhookId()))
                .findFirst()
                .orElse(null);

            if (existing != null) {
                webhookUrl = existing.getUrl();
            } else {
                Webhook created = channel.createWebhook(config.getWebhookId()).complete();
                webhookUrl = created.getUrl();
            }

            this.webhook = WebhookClient.withUrl(webhookUrl);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();

            LOG.error("Failed to initialize DBridge", ex);

            return;
        }

        ChatEventHandler chatHandler = new ChatEventHandler(webhook, inboundQueue);
        CommandEventHandler commandHandler = new CommandEventHandler(webhook);

        JoinLeaveEventHandler joinLeaveHandler = new JoinLeaveEventHandler(webhook);
        DeathEventHandler deathEventHandler = new DeathEventHandler(webhook);

        this.registerEventHandlers(
            chatHandler,
            commandHandler,
            joinLeaveHandler,
            deathEventHandler
        );
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        if (webhook != null) {
            webhook.close();
        }

        if (jda != null) {
            jda.shutdown();
        }
    }

    private void registerEventHandlers(Object... objs) {
        for (Object o : objs) {
            MinecraftForge.EVENT_BUS.register(o);
            FMLCommonHandler.instance().bus().register(o);
        }
    }
}
