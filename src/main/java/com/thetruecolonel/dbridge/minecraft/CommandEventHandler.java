package com.thetruecolonel.dbridge.minecraft;

import com.thetruecolonel.dbridge.util.WebhookUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import me.micartey.webhookly.DiscordWebhook;
import net.minecraftforge.event.CommandEvent;

public class CommandEventHandler {
    private final DiscordWebhook webhook;

    public CommandEventHandler(DiscordWebhook webhook) {
        this.webhook = webhook;
    }

    @SubscribeEvent
    public void onCommand(CommandEvent event) {
        // Handle `/say` command from console
        if (!event.command.getCommandName().equalsIgnoreCase("say"))
            return;

        String message = String.join(" ", event.parameters);

        WebhookUtils.sendSystemMessage(webhook, message);
    }
}
