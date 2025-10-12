package com.wairesdindustries.discordengine.common.discord.bot;

import com.wairesdindustries.discordengine.api.data.config.ConfigData;
import com.wairesdindustries.discordengine.api.discord.bot.DiscordBotService;
import com.wairesdindustries.discordengine.common.DiscordEngine;
import com.wairesdindustries.discordengine.common.discord.event.EventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.EnumSet;

public class DiscordBotServiceImpl implements DiscordBotService {

    private final DiscordEngine api;
    private JDA jda;

    public DiscordBotServiceImpl(DiscordEngine api) {
        this.api = api;
    }

    @Override
    public void connect() {
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

        } catch (SerializationException e) {
            throw new RuntimeException("Failed to load bot configuration", e);
        }
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
    
    /**
     * Provides the JDA instance to other services within the same package.
     * @return The JDA instance.
     */
    JDA getJda() {
        if (!isRunning()) {
            throw new IllegalStateException("JDA is not running or connected.");
        }
        return jda;
    }
}