package com.wairesdindustries.discordengine.api.discord.commands;

public interface DiscordCommandManager {
    void registerCommand(DiscordCommand command);
    void registerAll();
}
