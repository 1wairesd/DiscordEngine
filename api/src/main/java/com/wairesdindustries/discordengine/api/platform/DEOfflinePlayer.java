package com.wairesdindustries.discordengine.api.platform;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface DEOfflinePlayer {

    @Nullable String getName();

    @NotNull Object getHandler();

    @NotNull UUID getUniqueId();

}