package com.wairesdindustries.discordengine.api.discord.commands;

public interface CommandContext {
    void reply(String message);

    default void replyFormatted(String format, Object... args) {
        reply(String.format(format, args));
    }
}