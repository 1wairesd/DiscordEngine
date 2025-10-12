package com.wairesdindustries.discordengine.api.discord.bot;

import java.util.concurrent.CompletableFuture;

public interface DiscordMessaging {
    CompletableFuture<Void> sendMessageToChannel(String channelId, String message);
    CompletableFuture<Void> deleteCommand(String trigger);
    CompletableFuture<Void> deleteAllCommands();
}
