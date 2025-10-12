package com.wairesdindustries.discordengine.api.discord.actions;

import com.wairesdindustries.discordengine.api.discord.command.DiscordCommandContext;

public interface DiscordAction {
    void execute(DiscordCommandContext context);
}