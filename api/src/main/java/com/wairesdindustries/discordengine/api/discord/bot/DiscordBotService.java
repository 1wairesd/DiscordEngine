package com.wairesdindustries.discordengine.api.discord.bot;

import java.util.concurrent.CompletableFuture;

public interface DiscordBotService {

    CompletableFuture<Void> connect();

    void close();

    boolean isRunning();

    void sendMessageToChannel(String channelId, String message);
    
    void updateActivity(String activity);
    
    void updateAvatar(String avatarPath);
    
    String getBotName();
}
