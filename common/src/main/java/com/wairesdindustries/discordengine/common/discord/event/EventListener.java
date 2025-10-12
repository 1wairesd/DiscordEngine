package com.wairesdindustries.discordengine.common.discord.event;

import com.wairesdindustries.discordengine.common.DiscordEngine;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class EventListener implements net.dv8tion.jda.api.hooks.EventListener {

    private final DiscordEngine api;

    public EventListener(DiscordEngine api) {
        this.api = api;
    }

    @Override
    public void onEvent(GenericEvent event) {
        if (event instanceof MessageReceivedEvent msgEvent) {
            String author = msgEvent.getAuthor().getName();
            String content = msgEvent.getMessage().getContentRaw();
            api.getPlatform().getLogger().info("[Discord] " + author + ": " + content);
        }

    }
}
