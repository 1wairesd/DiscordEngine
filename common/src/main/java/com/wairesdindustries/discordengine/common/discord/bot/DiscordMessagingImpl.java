package com.wairesdindustries.discordengine.common.discord.bot;

import com.wairesdindustries.discordengine.api.discord.bot.DiscordBotService;
import com.wairesdindustries.discordengine.api.discord.bot.DiscordMessaging;

import java.util.concurrent.CompletableFuture;

public class DiscordMessagingImpl implements DiscordMessaging {

    private final DiscordBotService botService;

    public DiscordMessagingImpl(DiscordBotService botService) {
        this.botService = botService;
    }

    @Override
    public CompletableFuture<Void> sendMessageToChannel(String channelId, String message) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        try {
            botService.sendMessageToChannel(channelId, message);
            cf.complete(null);
        } catch (Exception e) {
            cf.completeExceptionally(e);
        }
        return cf;
    }

    @Override
    public CompletableFuture<Void> deleteCommand(String trigger) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> deleteAllCommands() {
        return CompletableFuture.completedFuture(null);
    }
}