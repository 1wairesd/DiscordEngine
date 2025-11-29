package com.wairesdindustries.discordengine.common;

import org.jetbrains.annotations.NotNull;
import com.wairesdindustries.discordengine.api.DEAPI;
import com.wairesdindustries.discordengine.api.discord.bot.DiscordAvatar;
import com.wairesdindustries.discordengine.api.discord.bot.DiscordBotManager;
import com.wairesdindustries.discordengine.api.discord.bot.DiscordBotService;
import com.wairesdindustries.discordengine.api.discord.bot.DiscordMessaging;
import com.wairesdindustries.discordengine.api.discord.command.DiscordCommandManager;
import com.wairesdindustries.discordengine.api.event.EventBus;
import com.wairesdindustries.discordengine.api.manager.ConfigManager;
import com.wairesdindustries.discordengine.api.manager.SubCommandManager;
import com.wairesdindustries.discordengine.api.platform.DEConfirmationManager;
import com.wairesdindustries.discordengine.common.config.ConfigManagerImpl;
import com.wairesdindustries.discordengine.common.confirmation.DEConfirmationManagerImpl;
import com.wairesdindustries.discordengine.common.discord.bot.DiscordAvatarImpl;
import com.wairesdindustries.discordengine.common.discord.bot.DiscordBotManagerImpl;
import com.wairesdindustries.discordengine.common.discord.bot.DiscordBotServiceImpl;
import com.wairesdindustries.discordengine.common.discord.bot.DiscordMessagingImpl;
import com.wairesdindustries.discordengine.common.discord.command.DiscordCommandManagerImpl;
import com.wairesdindustries.discordengine.common.discord.config.DiscordCommandLoader;
import com.wairesdindustries.discordengine.common.event.EventBusImpl;
import com.wairesdindustries.discordengine.common.event.EventListener;
import com.wairesdindustries.discordengine.common.manager.SubCommandManagerImpl;
import com.wairesdindustries.discordengine.common.platform.BackendPlatform;

public final class DiscordEngine extends DEAPI {

    private final BackendPlatform platform;
    private final SubCommandManagerImpl subCommandManager;
    private final ConfigManagerImpl configManager;
    private final DiscordCommandLoader commandLoader;
    private final DiscordCommandManagerImpl commandManager;
    private final EventBusImpl eventBus;
    private final EventListener eventListener;
    private final DEConfirmationManagerImpl confirmationManager;
    private final DiscordBotService botService;
    private final DiscordBotManager botManager;
    private final DiscordMessaging messagingService;
    private final DiscordAvatar avatarService;

    public DiscordEngine(BackendPlatform platform) {
        this.platform = platform;

        this.configManager = new ConfigManagerImpl(platform);
        this.subCommandManager = new SubCommandManagerImpl(this);
        this.commandLoader = new DiscordCommandLoader(this);
        this.botService = new DiscordBotServiceImpl(this);
        this.botManager = new DiscordBotManagerImpl(this);
        this.messagingService = new DiscordMessagingImpl(this.botService);
        this.avatarService = new DiscordAvatarImpl(this.botService, this);
        this.commandManager = new DiscordCommandManagerImpl(this);
        this.eventBus = new EventBusImpl(platform.getLogger());
        this.eventListener = new EventListener(this);
        this.confirmationManager = new DEConfirmationManagerImpl(this);

        DEAPI.instance = this;
    }

    public void load() {
        long time = System.currentTimeMillis();
        configManager.load();
        commandLoader.load();
        botManager.loadBots();
        botManager.connectAll();
        eventBus.register(eventListener);
        platform.getLogger().info("Enabled in " + (System.currentTimeMillis() - time) + "ms");
    }

    public void unload() {
        botManager.closeAll();
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

    @Override
    public @NotNull DiscordMessaging getDiscordMessagingService() {
        return messagingService;
    }

    @Override
    public @NotNull DiscordAvatar getDiscordAvatarService() {
        return avatarService;
    }

    @Override
    public @NotNull DiscordBotManager getDiscordBotManager() {
        return botManager;
    }

}