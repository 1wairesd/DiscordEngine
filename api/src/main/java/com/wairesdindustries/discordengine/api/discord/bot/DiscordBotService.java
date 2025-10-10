package com.wairesdindustries.discordengine.api.discord.bot;

public interface DiscordBotService {
    void connect();
    void close();
    void updateActivity();
    boolean isRunning();
    void deleteCommand(String trigger);
    void deleteAllCommands();
}
