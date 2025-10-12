package com.wairesdindustries.discordengine.common.discord.command;

import com.wairesdindustries.discordengine.api.discord.command.DiscordCommandContext;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class DiscordCommandContextImpl implements DiscordCommandContext {

    private final SlashCommandInteractionEvent slashEvent;
    private final MessageReceivedEvent messageEvent;

    public DiscordCommandContextImpl(SlashCommandInteractionEvent event) {
        this.slashEvent = event;
        this.messageEvent = null;
    }

    @Override
    public void reply(String message) {
        if (slashEvent != null) {
            slashEvent.reply(message).queue();
        } else if (messageEvent != null) {
            messageEvent.getChannel().sendMessage(message).queue();
        }
    }
}
