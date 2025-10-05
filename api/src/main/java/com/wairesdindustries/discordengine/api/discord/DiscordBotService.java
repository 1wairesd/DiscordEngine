package com.wairesdindustries.discordengine.api.discord;

public interface DiscordBotService {
    void connect();
    void close();
    void updateActivity();
    boolean isRunning();
}
