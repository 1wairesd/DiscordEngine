package com.wairesdindustries.discordengine.api.discord.command;

import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommand;

public interface DiscordCommandManager {

    void registerCommand(DiscordCommand command);

    void registerCommand(String name, String description);

    void registerAll();

}