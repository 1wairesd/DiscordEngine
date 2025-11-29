package com.wairesdindustries.discordengine.common.discord.bot;

import com.wairesdindustries.discordengine.api.discord.bot.DiscordAvatar;
import com.wairesdindustries.discordengine.api.discord.bot.DiscordBotService;
import com.wairesdindustries.discordengine.common.DiscordEngine;
import com.wairesdindustries.discordengine.common.discord.config.DiscordAvatarLoader;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class DiscordAvatarImpl implements DiscordAvatar {

    private final DiscordBotService botService;
    private final DiscordEngine api;

    public DiscordAvatarImpl(DiscordBotService botService, DiscordEngine api) {
        this.botService = botService;
        this.api = api;
    }

    @Override
    public CompletableFuture<Void> updateAvatar() {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        try {
            var config = api.getConfigManager().getConfig("Config.yml");
            if (config != null) {
                String avatarFile = config.node("bot", "sources", "avatar", "file").getString("avatar-discordengine-nofon.png");
                String avatarMode = config.node("bot", "sources", "avatar", "mode").getString("local");
                
                File avatar = new DiscordAvatarLoader(api).getAvatarFile(avatarFile, avatarMode);
                
                if (avatar.exists()) {
                    botService.updateAvatar(avatar.getAbsolutePath());
                }
            } else {
                File avatar = new DiscordAvatarLoader(api).getAvatarFile("avatar-discordengine-nofon.png", "global");
                if (avatar.exists()) {
                    botService.updateAvatar(avatar.getAbsolutePath());
                }
            }
            cf.complete(null);
        } catch (Exception e) {
            cf.completeExceptionally(e);
        }
        return cf;
    }

    @Override
    public CompletableFuture<Void> updateActivity(String activity) {
        botService.updateActivity(activity);
        return CompletableFuture.completedFuture(null);
    }
}