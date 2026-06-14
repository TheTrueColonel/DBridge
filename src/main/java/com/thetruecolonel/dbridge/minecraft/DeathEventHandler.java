package com.thetruecolonel.dbridge.minecraft;

import com.thetruecolonel.dbridge.util.PlayerUtils;
import com.thetruecolonel.dbridge.util.ServerConstants;
import com.thetruecolonel.dbridge.util.WebhookUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import me.micartey.webhookly.DiscordWebhook;
import me.micartey.webhookly.embeds.EmbedObject;
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

        String message = event.source.func_151519_b(player).getUnformattedTextForChat();

        String username = player.getDisplayName();

        WebhookUtils.fireWebhook(
                webhook,
                PlayerUtils.getAvatarUrl(username),
                ServerConstants.SERVER_NAME,
                this.buildEmbedFor(message)
        );
    }

    private EmbedObject buildEmbedFor(String message) {
        return new EmbedObject()
                .setColor(Color.RED)
                .setDescription(String.format("**%s**", message));
    }
}
