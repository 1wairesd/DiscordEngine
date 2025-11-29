package com.wairesdindustries.discordengine.api.discord.bot;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DiscordBotService {

    CompletableFuture<Void> connect();

    void close();

    boolean isRunning();

    void sendMessageToChannel(String channelId, String message);
    
    void sendEmbedToChannel(String channelId, Object embed);
    
    void updateActivity(String activity);
    
    void updateActivity(String type, String text);
    
    void updateAvatar(String avatarPath);
    
    void updateStatus(String status);
    
    String getBotName();
    
    Object getJDA();
    
    List<?> getGuilds();
    
    Object getGuildById(String guildId);
    
    Object getChannelById(String channelId);
    
    void createSlashCommand(String name, String description);
    
    void deleteSlashCommand(String commandId);
    
    void updateSlashCommands();
}
