package com.thetruecolonel.dbridge.minecraft;

import club.minnced.discord.webhook.WebhookClient;
import com.thetruecolonel.dbridge.util.WebhookUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.CommandEvent;

public class CommandEventHandler {
    private final WebhookClient webhook;

    public CommandEventHandler(WebhookClient webhook) {
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
