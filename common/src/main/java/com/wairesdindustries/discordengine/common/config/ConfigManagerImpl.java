package com.wairesdindustries.discordengine.common.config;

import com.wairesdindustries.discordengine.api.config.Messages;
import com.wairesdindustries.discordengine.api.config.converter.ConfigType;
import com.wairesdindustries.discordengine.api.config.converter.ConvertOrder;
import com.wairesdindustries.discordengine.api.data.config.ConfigData;
import com.wairesdindustries.discordengine.api.event.plugin.DiscordEngineReloadEvent;
import com.wairesdindustries.discordengine.api.manager.ConfigManager;
import com.wairesdindustries.discordengine.common.config.converter.ConfigConverter;
import com.wairesdindustries.discordengine.common.platform.BackendPlatform;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

public class ConfigManagerImpl implements ConfigManager {

    private final Map<String, ConfigImpl> configurations = new HashMap<>();
    private static final String[] defaultFiles = {
            "Config.yml",
    };

    @Getter
    private final BackendPlatform platform;

    private final ConfigConverter converter;
    private final MessagesImpl messages;

    public ConfigManagerImpl(BackendPlatform platform) {
        this.converter = new ConfigConverter(this);
        this.platform = platform;
        this.messages = new MessagesImpl(this);
    }

    @Override
    public void load() {
        configurations.clear();
        createFiles();
        loadConfigurations(platform.getDataFolder().listFiles(), false);

        try {
            ConfigData config = getConfig(true);
            if (config == null) {
                converter.convert(ConvertOrder.ON_CONFIG);
                load();
                return;
            }
            messages.load(config.language());


        } catch (ConfigurateException e) {
            platform.getLogger().log(Level.WARNING, "Error with loading configuration: ", e);
        }

        converter.convert(ConvertOrder.ON_CONFIG);

        platform.getAPI().getEventBus().post(new DiscordEngineReloadEvent(DiscordEngineReloadEvent.Type.CONFIG));
    }

    private void loadConfigurations(File[] files, boolean deep) {
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                String dirName = file.getName().toLowerCase();
                File[] subFiles = file.listFiles();

                if (deep || "commands".equals(dirName)) {
                    loadConfigurations(subFiles, true);
                } else if ("lang".equals(dirName)) {
                    loadConfigurations(subFiles, false);
                }
            } else {
                String fileName = file.getName().toLowerCase();
                if (fileName.endsWith(".yml") || fileName.endsWith(".yaml")) {
                    load(file);
                }
            }
        }
    }

    @Override
    @Nullable
    public ConfigurationNode getNode(@NotNull String name) {
        ConfigImpl config = getConfig(name);
        return config != null ? config.node() : null;
    }

    @Override
    public Map<String, ? extends ConfigImpl> get() {
        return configurations;
    }

    @Override
    public ConfigData getConfig() {
        return getConfig(false);
    }

    private ConfigData getConfig(boolean refresh) {
        ConfigImpl config = getConfig("Config.yml");
        if (config == null) {
            throw new IllegalStateException("Config.yml not loaded!");
        }
        return config.getSerialized(ConfigData.class);
    }

    @Override
    public @Nullable ConfigImpl getConfig(@NotNull String name) {
        return configurations.get("plugins/DiscordEngine/" + name);
    }

    @Override
    public @Nullable String getString(@NotNull String name, Object... path) {
        ConfigImpl config = getConfig(name);
        return config != null ? config.node().node(path).getString() : null;
    }

    @Override
    public @NotNull Optional<ConfigImpl> getConfig(@NotNull ConfigType type) {
        return configurations.values().stream().filter(value -> type.equals(value.type())).findFirst();
    }

    @Override
    public ConfigImpl load(@NotNull File file) {
        String path = file.getPath().replace("\\", "/");
        ConfigImpl exist = configurations.get(path);
        if (exist != null) return exist;

        ConfigImpl config = new ConfigImpl(file);

        try {
            config.load();
            configurations.put(config.path(), config);
        } catch (ConfigurateException e) {
            platform.getLogger().log(Level.WARNING, "Error with loading configuration: ", e);
        }

        return config;
    }

    @Override
    public @Nullable ConfigImpl unload(@NotNull String name) {
        return configurations.remove(name);
    }

    private void createFiles() {
        for (String fileName : defaultFiles) {
            File file = new File(platform.getDataFolder(), fileName);
            if (!file.exists()) platform.saveResource(fileName, false);
        }
    }

    @Override
    public @NotNull Messages getMessages() {
        return messages;
    }
}
