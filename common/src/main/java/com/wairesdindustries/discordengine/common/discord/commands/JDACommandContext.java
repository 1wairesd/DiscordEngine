package com.wairesdindustries.discordengine.common.discord.commands;

import com.wairesdindustries.discordengine.api.discord.commands.CommandContext;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class JDACommandContext implements CommandContext {

    private final SlashCommandInteractionEvent event;

    public JDACommandContext(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    @Override
    public void reply(String message) {
        event.reply(message).queue();
    }

    public SlashCommandInteractionEvent getEvent() {
        return event;
    }
}