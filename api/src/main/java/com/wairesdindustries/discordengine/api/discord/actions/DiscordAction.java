package com.wairesdindustries.discordengine.api.discord.actions;

import com.wairesdindustries.discordengine.api.discord.commands.DiscordCommandContext;

public interface DiscordAction {
    void execute(DiscordCommandContext context);
}