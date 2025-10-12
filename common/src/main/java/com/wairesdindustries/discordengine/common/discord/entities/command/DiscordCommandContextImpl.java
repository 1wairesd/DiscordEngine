package com.wairesdindustries.discordengine.common.discord.entities.command;

import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class DiscordCommandContextImpl implements DiscordCommandContext {

    private final SlashCommandInteractionEvent slashEvent;

    public DiscordCommandContextImpl(SlashCommandInteractionEvent event) {
        this.slashEvent = event;
    }

    @Override
    public void reply(String message) {
        if (this.slashEvent != null) {
            this.slashEvent.reply(message).queue();
        }
    }

    public SlashCommandInteractionEvent getSlashEvent() {
        return slashEvent;
    }
}
