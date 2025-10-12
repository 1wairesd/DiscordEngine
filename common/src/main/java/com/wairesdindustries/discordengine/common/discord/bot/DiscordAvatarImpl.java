package com.wairesdindustries.discordengine.common.discord.bot;

import com.wairesdindustries.discordengine.api.discord.bot.DiscordAvatar;
import com.wairesdindustries.discordengine.common.DiscordEngine;
import com.wairesdindustries.discordengine.api.data.config.ConfigData;
import com.wairesdindustries.discordengine.common.discord.config.DiscordAvatarLoader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Icon;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class DiscordAvatarImpl implements DiscordAvatar {

    private final DiscordBotServiceImpl botService;
    private final DiscordEngine api;

    public DiscordAvatarImpl(DiscordBotServiceImpl botService, DiscordEngine api) {
        this.botService = botService;
        this.api = api;
    }

    private JDA getJda() {
        return botService.getJda();
    }

    @Override
    public CompletableFuture<Void> updateAvatar() {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        try {
            ConfigData.Bot botConfig = api.getConfigManager()
                    .getConfig("Config.yml")
                    .node("bot")
                    .get(ConfigData.Bot.class);

            String avatarFile = botConfig != null ? botConfig.avatar() : null;
            File avatar = new DiscordAvatarLoader(api).getAvatarFile(avatarFile);

            if (avatar.exists()) {
                getJda().getSelfUser().getManager().setAvatar(Icon.from(avatar)).queue(cf::complete, cf::completeExceptionally);
            } else {
                cf.complete(null);
            }
        } catch (IOException e) {
            cf.completeExceptionally(e);
        }
        return cf;
    }

    @Override
    public CompletableFuture<Void> updateActivity(String activity) {
        getJda().getPresence().setActivity(Activity.playing(activity));
        return CompletableFuture.completedFuture(null);
    }
}