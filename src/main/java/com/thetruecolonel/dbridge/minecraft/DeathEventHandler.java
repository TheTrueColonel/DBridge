package com.thetruecolonel.dbridge.minecraft;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.thetruecolonel.dbridge.util.PlayerUtils;
import com.thetruecolonel.dbridge.util.ServerConstants;
import com.thetruecolonel.dbridge.util.WebhookUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.awt.Color;

public class DeathEventHandler {
    private final WebhookClient webhook;

    public DeathEventHandler(WebhookClient webhook) {
        this.webhook = webhook;
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        Entity e = event.entity;
        if (!(e instanceof EntityPlayer player))
            return;

        String message = event.source.func_151519_b(player).getUnformattedTextForChat();

        String username = player.getDisplayName();

        WebhookUtils.fireWebhook(
            webhook,
            PlayerUtils.getAvatarUrl(username),
            ServerConstants.SERVER_EVENT_NAME,
            this.buildEmbedFor(message)
        );
    }

    private WebhookEmbed buildEmbedFor(String message) {
        return new WebhookEmbedBuilder()
            .setColor(Color.RED.getRGB() & 0xFFFFFF)
            .setDescription(String.format("**%s**", message))
            .build();
    }
}
