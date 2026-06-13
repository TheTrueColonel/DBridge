package com.thetruecolonel.dbridge.minecraft;

import com.thetruecolonel.dbridge.util.PlayerUtils;
import com.thetruecolonel.dbridge.util.WebhookUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import me.micartey.webhookly.DiscordWebhook;
import me.micartey.webhookly.embeds.EmbedObject;
import me.micartey.webhookly.embeds.Thumbnail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.awt.Color;

public class DeathEventHandler {
    private final DiscordWebhook webhook;

    public DeathEventHandler(DiscordWebhook webhook) {
        this.webhook = webhook;
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        Entity e = event.entity;
        if (!(e instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) e;

        String message = event.source.func_151519_b(player).toString();

        String username = player.getDisplayName();

        WebhookUtils.fireWebhook(
                webhook,
                PlayerUtils.getAvatarUrl(username),
                username,
                this.buildEmbedFor(username, message)
        );
    }

    private EmbedObject buildEmbedFor(String username, String message) {
        return new EmbedObject()
                .setThumbnail(
                        new Thumbnail(PlayerUtils.getAvatarUrl(username))
                ).setColor(
                        Color.RED
                ).setDescription(
                        message
                );
    }
}
