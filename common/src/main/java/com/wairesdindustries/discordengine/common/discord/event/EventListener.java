package com.wairesdindustries.discordengine.common.discord.event;

import com.wairesdindustries.discordengine.common.DiscordEngine;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
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
        } else if (event instanceof SlashCommandInteractionEvent slashEvent) {
            api.getFlowManager().getFlowDispatcher().handleSlashCommand(slashEvent);
        } else if (event instanceof ButtonInteractionEvent buttonEvent) {
            api.getFlowManager().getFlowDispatcher().handleButtonInteraction(buttonEvent);
        } else if (event instanceof ModalInteractionEvent modalEvent) {
            api.getFlowManager().getFlowDispatcher().handleModalInteraction(modalEvent);
        }
    }
}
