package com.wairesdindustries.discordengine.api.discord.entities.actions;

import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;

public interface DiscordAction {
    void execute(DiscordCommandContext context);
}