package com.wairesdindustries.discordengine.api.discord.command;

public interface DiscordCommandManager {

    void registerCommand(DiscordCommand command);

    void registerAll();

}