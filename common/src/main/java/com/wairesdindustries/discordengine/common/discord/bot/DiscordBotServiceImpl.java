package com.wairesdindustries.discordengine.common.discord.bot;

import com.wairesdindustries.discordengine.api.data.config.ConfigData;
import com.wairesdindustries.discordengine.api.discord.bot.DiscordBotService;
import com.wairesdindustries.discordengine.common.DiscordEngine;
import com.wairesdindustries.discordengine.common.discord.event.EventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;

public class DiscordBotServiceImpl implements DiscordBotService {

    private final DiscordEngine api;
    private JDA jda;

    public DiscordBotServiceImpl(DiscordEngine api) {
        this.api = api;
    }

    @Override
    public CompletableFuture<Void> connect() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        CompletableFuture.runAsync(() -> {
            try {
                ConfigData.Bot botConfig = api.getConfigManager()
                        .getConfig("Config.yml")
                        .node("bot")
                        .get(ConfigData.Bot.class);

                String token = botConfig != null ? botConfig.token() : null;

                this.jda = JDABuilder.createDefault(token, EnumSet.of(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT
                                ))
                        .addEventListeners(new EventListener(api), api.getDiscordCommandManager())
                        .build();

                future.complete(null);
            } catch (SerializationException e) {
                future.completeExceptionally(e);
            }
        });
        
        return future;
    }

    @Override
    public synchronized void close() {
        if (jda != null) {
            jda.shutdown();
            jda = null;
        }
    }

    @Override
    public boolean isRunning() {
        return jda != null && jda.getStatus() == JDA.Status.CONNECTED;
    }
    
    @Override
    public void sendMessageToChannel(String channelId, String message) {
        if (jda != null && jda.getStatus() == JDA.Status.CONNECTED) {
            jda.getTextChannelById(channelId).sendMessage(message).queue();
        }
    }

    @Override
    public void updateAvatar(String avatarPath) {
        if (jda != null && jda.getStatus() == JDA.Status.CONNECTED) {
            try {
                File avatarFile = new java.io.File(avatarPath);
                if (avatarFile.exists()) {
                    jda.getSelfUser().getManager().setAvatar(Icon.from(avatarFile)).queue();
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void updateActivity(String activity) {
        if (jda != null && jda.getStatus() == JDA.Status.CONNECTED) {
            jda.getPresence().setActivity(Activity.playing(activity));
        }
    }
}