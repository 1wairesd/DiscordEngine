package com.wairesdindustries.discordengine.common.discord.bot;

import com.wairesdindustries.discordengine.api.data.config.ConfigData;
import com.wairesdindustries.discordengine.api.discord.bot.DiscordBotService;
import com.wairesdindustries.discordengine.common.DiscordEngine;
import com.wairesdindustries.discordengine.common.discord.config.DiscordAvatarLoader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class DiscordBotServiceImpl implements DiscordBotService {

    private final DiscordEngine api;
    private JDA jda;

    public DiscordBotServiceImpl(DiscordEngine api) {
        this.api = api;
    }

    @Override
    public synchronized void connect() {
        try {
            ConfigData.Bot botConfig = api.getConfigManager()
                    .getConfig("Config.yml")
                    .node("bot")
                    .get(ConfigData.Bot.class);

            String token = botConfig != null ? botConfig.token() : null;
            String activity = botConfig != null ? botConfig.activity() : null;

            if (token == null) {
                throw new IllegalStateException("Bot token is missing in config!");
            }

            jda = net.dv8tion.jda.api.JDABuilder.createDefault(
                            token,
                            EnumSet.of(
                                    GatewayIntent.GUILD_MESSAGES,
                                    GatewayIntent.MESSAGE_CONTENT)
                    )
                    .setActivity(Activity.playing(activity != null ? activity : "Discord Engine"))
                    .build();

            jda.awaitReady();
            updateAvatar();
        } catch (SerializationException e) {
            throw new RuntimeException("Failed to load bot configuration", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            api.getPlatform().getLogger().warning("Bot connect interrupted while waiting for readiness");
        }
    }



    @Override
    public synchronized void close() {
        if (jda == null) return;
        try {
            jda.shutdown();
            jda.awaitShutdown(5, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        } finally {
            if (jda != null && jda.getStatus() != JDA.Status.SHUTDOWN) {
                jda.shutdownNow();
            }
            jda = null;
        }
    }


    @Override
    public synchronized void updateActivity() {
        if (jda != null) {
            try {
                ConfigData.Bot botConfig = api.getConfigManager()
                        .getConfig("Config.yml")
                        .node("bot")
                        .get(ConfigData.Bot.class);

                String activity = botConfig != null ? botConfig.activity() : "Discord Engine";
                jda.getPresence().setActivity(Activity.playing(activity));
            } catch (SerializationException e) {
                api.getPlatform().getLogger().warning("Failed to update bot activity: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean isRunning() {
        return jda != null && jda.getStatus() != JDA.Status.SHUTDOWN;
    }

    public JDA getJDA() {
        return jda;
    }

    @Override
    public void deleteCommand(String trigger) {
        if (jda == null) return;

        jda.updateCommands().queue(commands -> {
            commands.stream()
                    .filter(cmd -> cmd.getName().equalsIgnoreCase(trigger))
                    .forEach(cmd -> jda.deleteCommandById(cmd.getId()).queue());
        });
    }

    @Override
    public synchronized void deleteAllCommands() {
        if (jda == null) return;
        jda.updateCommands().queue(commands -> {
            commands.forEach(cmd -> jda.deleteCommandById(cmd.getId()).queue());
        });
    }

    @Override
    public synchronized void updateAvatar() {
        if (jda == null) return;

        try {
            ConfigData.Bot botConfig = api.getConfigManager()
                    .getConfig("Config.yml")
                    .node("bot")
                    .get(ConfigData.Bot.class);

            String avatarFile = botConfig != null ? botConfig.avatar() : null;
            File avatar = new DiscordAvatarLoader(api).getAvatarFile(avatarFile);

            if (avatar.exists()) {
                jda.getSelfUser().getManager().setAvatar(Icon.from(avatar)).queue();
            }
        } catch (IOException e) {
            api.getPlatform().getLogger().warning("Failed to update bot avatar: " + e.getMessage());
        }
    }
}
