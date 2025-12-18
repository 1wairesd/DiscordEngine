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
        
        try {
            Class<?> jdaLoggerClass = Class.forName("net.dv8tion.jda.internal.utils.JDALogger");
            java.lang.reflect.Method method = jdaLoggerClass.getMethod("setFallbackLoggerEnabled", boolean.class);
            method.invoke(null, false);
        } catch (Exception e) {
           
        }
    }

    @Override
    public CompletableFuture<Void> connect() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        CompletableFuture.runAsync(() -> {
            try {
                String configPath = "bots/" + botName + "/discord/engine.yml";

                File configFile = new File(api.getPlatform().getDataFolder(), configPath);
                if (!configFile.exists()) {
                    future.complete(null);
                    return;
                }

                api.getConfigManager().load(configFile);
                
                var config = api.getConfigManager().getConfig(configPath);
                if (config == null) {
                    future.complete(null);
                    return;
                }
                
                BotConfigData botConfig = config.node("bot").get(BotConfigData.class);

                String token = botConfig != null ? botConfig.getToken() : null;
                
                if (token == null || token.equals("your-bot-token")) {
                    future.complete(null);
                    return;
                }

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

                if (botConfig.getAvatar() != null && botConfig.getAvatar().getFile() != null) {
                    loadAndSetAvatar(botConfig.getAvatar().getFile());
                }

                future.complete(null);
            } catch (SerializationException e) {
                api.getPlatform().getLogger().severe("Failed to load config for bot '" + botName + "': " + e.getMessage());
                future.completeExceptionally(e);
            } catch (Exception e) {
                api.getPlatform().getLogger().severe("Failed to connect bot '" + botName + "': " + e.getMessage());
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

    @Override
    public void sendEmbedToChannel(String channelId, Object embed) {
        if (jda != null && jda.getStatus() == JDA.Status.CONNECTED && embed instanceof net.dv8tion.jda.api.entities.MessageEmbed) {
            jda.getTextChannelById(channelId).sendMessageEmbeds((net.dv8tion.jda.api.entities.MessageEmbed) embed).queue();
        }
    }

    @Override
    public void updateActivity(String type, String text) {
        if (jda != null && jda.getStatus() == JDA.Status.CONNECTED) {
            Activity activity;
            switch (type.toUpperCase()) {
                case "PLAYING":
                    activity = Activity.playing(text);
                    break;
                case "WATCHING":
                    activity = Activity.watching(text);
                    break;
                case "LISTENING":
                    activity = Activity.listening(text);
                    break;
                case "COMPETING":
                    activity = Activity.competing(text);
                    break;
                case "STREAMING":
                    activity = Activity.streaming(text, "https://twitch.tv/");
                    break;
                default:
                    activity = Activity.playing(text);
            }
            jda.getPresence().setActivity(activity);
        }
    }

    @Override
    public void updateStatus(String status) {
        if (jda != null && jda.getStatus() == JDA.Status.CONNECTED) {
            net.dv8tion.jda.api.OnlineStatus onlineStatus;
            switch (status.toUpperCase()) {
                case "ONLINE":
                    onlineStatus = net.dv8tion.jda.api.OnlineStatus.ONLINE;
                    break;
                case "IDLE":
                    onlineStatus = net.dv8tion.jda.api.OnlineStatus.IDLE;
                    break;
                case "DND":
                case "DO_NOT_DISTURB":
                    onlineStatus = net.dv8tion.jda.api.OnlineStatus.DO_NOT_DISTURB;
                    break;
                case "INVISIBLE":
                    onlineStatus = net.dv8tion.jda.api.OnlineStatus.INVISIBLE;
                    break;
                default:
                    onlineStatus = net.dv8tion.jda.api.OnlineStatus.ONLINE;
            }
            jda.getPresence().setStatus(onlineStatus);
        }
    }

    @Override
    public Object getJDA() {
        return jda;
    }

    @Override
    public java.util.List<?> getGuilds() {
        return jda != null ? jda.getGuilds() : java.util.Collections.emptyList();
    }

    @Override
    public Object getGuildById(String guildId) {
        return jda != null ? jda.getGuildById(guildId) : null;
    }

    @Override
    public Object getChannelById(String channelId) {
        return jda != null ? jda.getTextChannelById(channelId) : null;
    }

    @Override
    public void createSlashCommand(String name, String description) {
        if (jda != null && jda.getStatus() == JDA.Status.CONNECTED) {
            jda.upsertCommand(name, description).queue();
        }
    }

    @Override
    public void deleteSlashCommand(String commandId) {
        if (jda != null && jda.getStatus() == JDA.Status.CONNECTED) {
            jda.deleteCommandById(commandId).queue();
        }
    }

    @Override
    public void updateSlashCommands() {
        if (jda != null && jda.getStatus() == JDA.Status.CONNECTED) {
            jda.updateCommands().queue();
        }
    }
    
    private void loadAndSetAvatar(String avatarFileName) {
        if (jda == null || avatarFileName == null || avatarFileName.trim().isEmpty()) {
            return;
        }
        
        try {
            File assetsDir = new File(api.getPlatform().getDataFolder(), "bots/" + botName + "/discord/assets");
            File avatarFile = new File(assetsDir, avatarFileName);
            
            if (avatarFile.exists()) {
                jda.getSelfUser().getManager().setAvatar(Icon.from(avatarFile)).queue();
            } else {
                api.getPlatform().getLogger().warning("Avatar file not found: " + avatarFile.getAbsolutePath());
            }
        } catch (Exception e) {
            api.getPlatform().getLogger().warning("Failed to load avatar for bot '" + botName + "': " + e.getMessage());
        }
    }
}