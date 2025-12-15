package com.wairesdindustries.discordengine.common.discord.bot;

import java.util.concurrent.CompletableFuture;

import com.wairesdindustries.discordengine.api.discord.bot.DiscordAvatar;
import com.wairesdindustries.discordengine.api.discord.bot.DiscordBotService;
import com.wairesdindustries.discordengine.common.DiscordEngine;

public class DiscordAvatarImpl implements DiscordAvatar {

    private final DiscordBotService botService;

    public DiscordAvatarImpl(DiscordBotService botService, DiscordEngine api) {
        this.botService = botService;
    }

    @Override
    public CompletableFuture<Void> updateAvatar() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> updateActivity(String activity) {
        botService.updateActivity(activity);
        return CompletableFuture.completedFuture(null);
    }
}