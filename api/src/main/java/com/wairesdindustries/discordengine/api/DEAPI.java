package com.wairesdindustries.discordengine.api;

import com.wairesdindustries.discordengine.api.config.Loadable;
import com.wairesdindustries.discordengine.api.discord.bot.DiscordAvatar;
import com.wairesdindustries.discordengine.api.discord.bot.DiscordBotService;
import com.wairesdindustries.discordengine.api.discord.bot.DiscordMessaging;
import com.wairesdindustries.discordengine.api.discord.command.DiscordCommandManager;
import com.wairesdindustries.discordengine.api.event.EventBus;
import com.wairesdindustries.discordengine.api.manager.SubCommandManager;
import com.wairesdindustries.discordengine.api.manager.ConfigManager;
import com.wairesdindustries.discordengine.api.platform.DEConfirmationManager;
import com.wairesdindustries.discordengine.api.platform.Platform;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public abstract class DEAPI {

    @Getter
    protected static DEAPI instance;

    public abstract @NotNull SubCommandManager getSubCommandManager();

    public abstract @NotNull ConfigManager getConfigManager();

    public abstract @NotNull Platform getPlatform();

    public abstract @NotNull DEConfirmationManager getConfirmationManager();

    public abstract @NotNull Loadable getDiscordCommandLoader();

    public abstract @NotNull DiscordCommandManager getDiscordCommandManager();

    public abstract @NotNull DiscordBotService getDiscordBotService();

    public abstract @NotNull EventBus getEventBus();

    public abstract @NotNull DiscordAvatar getDiscordAvatarService();

    public abstract @NotNull DiscordMessaging getDiscordMessagingService();

}