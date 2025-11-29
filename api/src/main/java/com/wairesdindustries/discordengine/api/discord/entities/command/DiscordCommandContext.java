package com.wairesdindustries.discordengine.api.discord.entities.command;

import java.util.List;

public interface DiscordCommandContext {

    void reply(String message);
    
    void replyEphemeral(String message);
    
    void replyEmbed(Object embed);
    
    void deferReply();
    
    void deferReplyEphemeral();

    default void replyFormatted(String format, Object... args) {
        reply(String.format(format, args));
    }
    
    Object getUser();
    
    Object getMember();
    
    Object getGuild();
    
    Object getChannel();
    
    String getOptionAsString(String name);
    
    Long getOptionAsLong(String name);
    
    Integer getOptionAsInteger(String name);
    
    Boolean getOptionAsBoolean(String name);
    
    Object getOptionAsUser(String name);
    
    Object getOptionAsMember(String name);
    
    Object getOptionAsRole(String name);
    
    Object getOptionAsChannel(String name);
    
    List<?> getOptions();
    
    void addReaction(String emoji);
    
    void sendPrivateMessage(String message);
    
    boolean hasPermission(String permission);

}