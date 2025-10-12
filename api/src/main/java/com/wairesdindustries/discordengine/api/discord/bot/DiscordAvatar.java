package com.wairesdindustries.discordengine.api.discord.bot;

import java.util.concurrent.CompletableFuture;

public interface DiscordAvatar {
    CompletableFuture<Void> updateAvatar();
    CompletableFuture<Void> updateActivity(String activity);
}
