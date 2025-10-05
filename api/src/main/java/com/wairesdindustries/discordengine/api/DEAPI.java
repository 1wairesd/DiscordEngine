package com.wairesdindustries.discordengine.api;

import com.wairesdindustries.discordengine.api.config.Loadable;
import com.wairesdindustries.discordengine.api.discord.DiscordBotService;
import com.wairesdindustries.discordengine.api.event.EventBus;
import com.wairesdindustries.discordengine.api.manager.SubCommandManager;
import com.wairesdindustries.discordengine.api.manager.ConfigManager;
import com.wairesdindustries.discordengine.api.platform.Platform;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public abstract class DEAPI {

    @Getter
    protected static DEAPI instance;

    public abstract @NotNull SubCommandManager getSubCommandManager();

    public abstract @NotNull ConfigManager getConfigManager();

    public abstract @NotNull Platform getPlatform();

    public abstract @NotNull Loadable getCommandLoader();

    public abstract @NotNull Object getCommandManager();

    public abstract @NotNull DiscordBotService getBotService();

    public abstract @NotNull EventBus getEventBus();

}