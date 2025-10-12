package com.wairesdindustries.discordengine.common;

import com.wairesdindustries.discordengine.api.DEAPI;
import com.wairesdindustries.discordengine.api.discord.bot.DiscordBotService;
import com.wairesdindustries.discordengine.api.discord.command.DiscordCommandManager;
import com.wairesdindustries.discordengine.api.event.EventBus;
import com.wairesdindustries.discordengine.api.manager.ConfigManager;
import com.wairesdindustries.discordengine.api.manager.SubCommandManager;
import com.wairesdindustries.discordengine.api.platform.DEConfirmationManager;
import com.wairesdindustries.discordengine.common.config.ConfigManagerImpl;
import com.wairesdindustries.discordengine.common.confirmation.DEConfirmationManagerImpl;
import com.wairesdindustries.discordengine.common.discord.bot.DiscordBotServiceImpl;
import com.wairesdindustries.discordengine.common.discord.command.DiscordCommandManagerImpl;
import com.wairesdindustries.discordengine.common.discord.config.DiscordCommandLoader;
import com.wairesdindustries.discordengine.common.event.EventBusImpl;
import com.wairesdindustries.discordengine.common.event.EventListener;
import com.wairesdindustries.discordengine.common.manager.SubCommandManagerImpl;
import com.wairesdindustries.discordengine.common.platform.BackendPlatform;
import org.jetbrains.annotations.NotNull;

public final class DiscordEngine extends DEAPI {

    private final BackendPlatform platform;
    private final SubCommandManagerImpl subCommandManager;
    private final ConfigManagerImpl configManager;
    private final DiscordCommandLoader commandLoader;
    private final DiscordCommandManagerImpl commandManager;
    private final DiscordBotServiceImpl botService;
    private final EventBusImpl eventBus;
    private final EventListener eventListener;
    private final DEConfirmationManagerImpl confirmationManager;

    public DiscordEngine(BackendPlatform platform) {
        this.platform = platform;

        this.configManager = new ConfigManagerImpl(platform);
        this.subCommandManager = new SubCommandManagerImpl(this);
        this.commandLoader = new DiscordCommandLoader(this);
        this.commandManager = new DiscordCommandManagerImpl(this);
        this.botService = new DiscordBotServiceImpl(this);
        this.eventBus = new EventBusImpl(platform.getLogger());
        this.eventListener = new EventListener(this);
        this.confirmationManager = new DEConfirmationManagerImpl(this);

        DEAPI.instance = this;
    }

    public void load() {
        long time = System.currentTimeMillis();
        configManager.load();
        commandLoader.load();
        botService.connect();
        commandManager.registerAll();
        eventBus.register(eventListener);
        platform.getLogger().info("Enabled in " + (System.currentTimeMillis() - time) + "ms");
    }

    public void unload() {
        botService.close();
        eventBus.unregister(eventListener);
    }

    @Override
    public @NotNull SubCommandManager getSubCommandManager() {
        return subCommandManager;
    }

    @Override
    public @NotNull ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public @NotNull BackendPlatform getPlatform() {
        return platform;
    }

    @Override
    public @NotNull DiscordCommandLoader getDiscordCommandLoader() {
        return commandLoader;
    }

    @Override
    public @NotNull DiscordCommandManager getDiscordCommandManager() {
        return commandManager;
    }

    @Override
    public @NotNull DiscordBotService getDiscordBotService() {
        return botService;
    }

    @Override
    public @NotNull EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public @NotNull DEConfirmationManager getConfirmationManager() {
        return confirmationManager;
    }

}