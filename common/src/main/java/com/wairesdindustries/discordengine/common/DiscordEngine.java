package com.wairesdindustries.discordengine.common;

import com.wairesdindustries.discordengine.api.discord.flow.FlowManager;
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
import com.wairesdindustries.discordengine.common.confirmation.ConfirmationManagerImpl;
import com.wairesdindustries.discordengine.common.discord.bot.DiscordAvatarImpl;
import com.wairesdindustries.discordengine.common.discord.bot.DiscordBotManagerImpl;
import com.wairesdindustries.discordengine.common.discord.bot.DiscordBotServiceImpl;
import com.wairesdindustries.discordengine.common.discord.bot.DiscordMessagingImpl;
import com.wairesdindustries.discordengine.common.discord.command.DiscordCommandManagerImpl;
import com.wairesdindustries.discordengine.common.discord.event.EventListener;
import com.wairesdindustries.discordengine.common.discord.flow.FlowManagerImpl;
import com.wairesdindustries.discordengine.common.event.EventBusImpl;
import com.wairesdindustries.discordengine.common.manager.SubCommandManagerImpl;
import com.wairesdindustries.discordengine.common.platform.BackendPlatform;

public final class DiscordEngine extends DEAPI {

    private final BackendPlatform platform;
    private final SubCommandManagerImpl subCommandManager;
    private final ConfigManagerImpl configManager;
    private final FlowManagerImpl flowManager;
    private final DiscordCommandManagerImpl commandManager;
    private final EventBusImpl eventBus;
    private final EventListener eventListener;
    private final ConfirmationManagerImpl confirmationManager;
    private final DiscordBotService botService;
    private final DiscordBotManager botManager;
    private final DiscordMessaging messagingService;
    private final DiscordAvatar avatarService;

    public DiscordEngine(BackendPlatform platform) {
        this.platform = platform;

        this.configManager = new ConfigManagerImpl(platform);
        this.subCommandManager = new SubCommandManagerImpl(this);
        this.flowManager = new FlowManagerImpl(this);
        this.botService = new DiscordBotServiceImpl(this);
        this.botManager = new DiscordBotManagerImpl(this);
        this.messagingService = new DiscordMessagingImpl(this.botService);
        this.avatarService = new DiscordAvatarImpl(this.botService, this);
        this.commandManager = new DiscordCommandManagerImpl(this);
        this.eventBus = new EventBusImpl(platform.getLogger());
        this.eventListener = new EventListener(this);
        this.confirmationManager = new ConfirmationManagerImpl(this);

        DEAPI.instance = this;
    }

    public void load() {
        long time = System.currentTimeMillis();
        configManager.load();
        botManager.loadBots();
        flowManager.load();
        botManager.connectAll();
        platform.getLogger().info("Enabled in " + (System.currentTimeMillis() - time) + "ms");
    }

    public void unload() {
        botManager.closeAll();
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
    public @NotNull FlowManager getFlowManager() {
        return flowManager;
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