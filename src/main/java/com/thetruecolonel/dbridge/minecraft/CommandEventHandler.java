package com.thetruecolonel.dbridge.minecraft;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import me.micartey.webhookly.DiscordWebhook;
import net.minecraftforge.event.CommandEvent;

import java.io.IOException;

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

        webhook.setUsername("Server");
        webhook.setAvatarUrl("");
        webhook.setContent(message);

        try {
            webhook.execute();
        } catch (IOException exception) {
            // Do nothing
        }
    }
}
