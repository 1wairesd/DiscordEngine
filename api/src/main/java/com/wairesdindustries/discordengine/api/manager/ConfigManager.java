package com.wairesdindustries.discordengine.api.manager;

import com.wairesdindustries.discordengine.api.config.Config;
import com.wairesdindustries.discordengine.api.config.Loadable;
import com.wairesdindustries.discordengine.api.config.Messages;
import com.wairesdindustries.discordengine.api.config.converter.ConfigType;
import com.wairesdindustries.discordengine.api.data.config.ConfigData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.util.Map;
import java.util.Optional;

public interface ConfigManager extends Loadable {

    Config load(@NotNull File file);

    @Nullable Config unload(@NotNull String name);

    @Nullable Config getConfig(@NotNull String name);

    @Nullable String getString(@NotNull String name, Object... path);

    @NotNull Optional<? extends Config> getConfig(@NotNull ConfigType type);

    @NotNull Messages getMessages();

    ConfigData getConfig();

    Map<String, ? extends Config> get();

    @Nullable ConfigurationNode getNode(@NotNull String name);

}