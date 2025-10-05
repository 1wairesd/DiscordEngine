package com.wairesdindustries.discordengine.common;

import com.wairesdindustries.discordengine.api.DEAPI;
import com.wairesdindustries.discordengine.api.event.EventBus;
import com.wairesdindustries.discordengine.common.config.ConfigManagerImpl;
import com.wairesdindustries.discordengine.common.discord.JDACommandManager;
import com.wairesdindustries.discordengine.common.discord.bot.DiscordBotServiceImpl;
import com.wairesdindustries.discordengine.common.discord.config.CommandLoader;
import com.wairesdindustries.discordengine.common.event.EventBusImpl;
import com.wairesdindustries.discordengine.common.event.EventListener;
import com.wairesdindustries.discordengine.common.manager.SubCommandManagerImpl;
import com.wairesdindustries.discordengine.common.platform.BackendPlatform;
import org.jetbrains.annotations.NotNull;

public final class DiscordEngine extends DEAPI {

    private final BackendPlatform platform;
    private final SubCommandManagerImpl subCommandManager;
    private final ConfigManagerImpl configManager;
    private final CommandLoader commandLoader;
    private final JDACommandManager commandManager;
    private final DiscordBotServiceImpl botService;
    private final EventBusImpl eventBus;
    private final EventListener eventListener;

    public DiscordEngine(BackendPlatform platform) {
        this.platform = platform;

        this.configManager = new ConfigManagerImpl(platform);
        this.subCommandManager = new SubCommandManagerImpl(this);
        this.commandLoader = new CommandLoader(this);
        this.commandManager = new JDACommandManager(this);
        this.botService = new DiscordBotServiceImpl(this);
        this.eventBus = new EventBusImpl(platform.getLogger());
        this.eventListener = new EventListener(this);

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
    public @NotNull SubCommandManagerImpl getSubCommandManager() {
        return subCommandManager;
    }

    @Override
    public @NotNull ConfigManagerImpl getConfigManager() {
        return configManager;
    }

    @Override
    public @NotNull BackendPlatform getPlatform() {
        return platform;
    }

    @Override
    public @NotNull CommandLoader getCommandLoader() {
        return commandLoader;
    }

    @Override
    public @NotNull JDACommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public @NotNull DiscordBotServiceImpl getBotService() {
        return botService;
    }

    @Override
    public @NotNull EventBus getEventBus() {
        return eventBus;
    }

}
