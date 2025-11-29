package com.wairesdindustries.discordengine.common.discord.bot;

import java.io.File;
import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.configurate.serialize.SerializationException;

import com.wairesdindustries.discordengine.api.data.config.BotConfigData;
import com.wairesdindustries.discordengine.api.discord.bot.DiscordBotService;
import com.wairesdindustries.discordengine.common.DiscordEngine;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordBotServiceImpl implements DiscordBotService {

    private final DiscordEngine api;
    private final String botName;
    private JDA jda;

    public DiscordBotServiceImpl(DiscordEngine api) {
        this(api, "default");
    }

    public DiscordBotServiceImpl(DiscordEngine api, String botName) {
        this.api = api;
        this.botName = botName;
    }

    @Override
    public CompletableFuture<Void> connect() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        CompletableFuture.runAsync(() -> {
            try {
                String configPath = "bots/" + botName + "/discord/Config.yml";

                File configFile = new File(api.getPlatform().getDataFolder(), configPath);
                if (!configFile.exists()) {
                    api.getPlatform().getLogger().warning("[DiscordEngine] Config file not found for bot '" + botName + "': " + configPath);
                    future.complete(null);
                    return;
                }

                api.getConfigManager().load(configFile);
                
                var config = api.getConfigManager().getConfig(configPath);
                if (config == null) {
                    api.getPlatform().getLogger().severe("[DiscordEngine] Failed to load config for bot '" + botName + "'. Check the config file format.");
                    future.complete(null);
                    return;
                }
                
                BotConfigData botConfig = config.node("bot").get(BotConfigData.class);

                String token = botConfig != null ? botConfig.getToken() : null;
                
                if (token == null || token.equals("your-bot-token")) {
                    api.getPlatform().getLogger().warning("[DiscordEngine] Bot '" + botName + "' has no valid token. Please set a valid Discord bot token in bots/" + botName + "/discord/Config.yml");
                    future.complete(null);
                    return;
                }
                
                api.getPlatform().getLogger().info("[DiscordEngine] Connecting bot '" + botName + "' to Discord...");

                this.jda = JDABuilder.createDefault(token, EnumSet.of(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT
                                ))
                        .disableCache(net.dv8tion.jda.api.utils.cache.CacheFlag.VOICE_STATE,
                                      net.dv8tion.jda.api.utils.cache.CacheFlag.EMOJI,
                                      net.dv8tion.jda.api.utils.cache.CacheFlag.STICKER,
                                      net.dv8tion.jda.api.utils.cache.CacheFlag.SCHEDULED_EVENTS)
                        .addEventListeners(new com.wairesdindustries.discordengine.common.discord.event.EventListener(api), api.getDiscordCommandManager())
                        .build();
                
                api.getPlatform().getLogger().info("[DiscordEngine] Bot '" + botName + "' connected successfully!");
                future.complete(null);
            } catch (SerializationException e) {
                api.getPlatform().getLogger().severe("[DiscordEngine] Failed to load config for bot '" + botName + "': " + e.getMessage());
                future.completeExceptionally(e);
            } catch (Exception e) {
                api.getPlatform().getLogger().severe("[DiscordEngine] Failed to connect bot '" + botName + "': " + e.getMessage());
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
    
    @Override
    public String getBotName() {
        return botName;
    }
}