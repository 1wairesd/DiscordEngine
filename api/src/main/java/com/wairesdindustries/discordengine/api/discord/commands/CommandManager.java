package com.wairesdindustries.discordengine.api.discord.commands;

public interface CommandManager {
    void registerCommand(Command command);
    void registerAll();
}
