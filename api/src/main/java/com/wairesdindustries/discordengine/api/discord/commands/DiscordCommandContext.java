package com.wairesdindustries.discordengine.api.discord.commands;

public interface DiscordCommandContext {
    void reply(String message);

    default void replyFormatted(String format, Object... args) {
        reply(String.format(format, args));
    }
}