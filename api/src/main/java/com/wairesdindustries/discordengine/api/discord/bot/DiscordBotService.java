package com.wairesdindustries.discordengine.api.discord.bot;

public interface DiscordBotService {

    void connect();

    void close();

    boolean isRunning();

}
