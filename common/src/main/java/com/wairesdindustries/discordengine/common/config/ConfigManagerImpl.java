package com.wairesdindustries.discordengine.common.config;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import com.wairesdindustries.discordengine.api.config.Config;
import com.wairesdindustries.discordengine.api.config.Messages;
import com.wairesdindustries.discordengine.api.config.converter.ConfigType;
import com.wairesdindustries.discordengine.api.config.converter.ConvertOrder;
import com.wairesdindustries.discordengine.api.data.config.ConfigData;
import com.wairesdindustries.discordengine.api.event.plugin.DiscordEngineReloadEvent;
import com.wairesdindustries.discordengine.api.manager.ConfigManager;
import com.wairesdindustries.discordengine.common.config.converter.ConfigConverter;
import com.wairesdindustries.discordengine.common.config.converter.DefaultConfigType;
import com.wairesdindustries.discordengine.common.platform.BackendPlatform;

import lombok.Getter;

public class ConfigManagerImpl implements ConfigManager {

    private ConfigData configData;
    private final Map<String, ConfigImpl> configurations = new HashMap<>();
    private static final String[] defaultFiles = {
            "Config.yml",
            "Bots.yml"
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

                if (deep || "bots".equals(dirName) || "discord".equals(dirName) || 
                    "commands".equals(dirName) || "buttons".equals(dirName) || 
                    "flows".equals(dirName) || "modals".equals(dirName)) {
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
        if (!refresh && configData != null) return configData;

        Config config = getConfig("Config.yml");

        if (config == null) {
            config = getConfig(DefaultConfigType.CONFIG).orElse(null);
            if (config == null) {
                throw new IllegalStateException("DiscordEngine config is null!");
            }
        }

        configData = config.getSerialized(ConfigData.class);

        return configData;
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

    public void copyBotDefaultResources(String botName) {
        File botDir = new File(platform.getDataFolder(), "bots/" + botName);
        File discordDir = new File(botDir, "discord");

        new File(discordDir, "assets").mkdirs();

        File configFile = new File(discordDir, "engine.yml");

        File oldConfigFile = new File(discordDir, "Config.yml");
        if (oldConfigFile.exists() && !configFile.exists()) {
            try {
                String oldContent = Files.readString(oldConfigFile.toPath());
                String newContent = oldContent.replace("discord-config-bot", "discord-engine");
                Files.writeString(configFile.toPath(), newContent);
            } catch (Exception e) {
                platform.getLogger().warning("Failed to migrate config file: " + e.getMessage());
            }
        }
        
        if (!configFile.exists()) {
            try (java.io.InputStream is = platform.getResource("bots/default/discord/engine.yml")) {
                if (is != null) {
                    Files.copy(is, configFile.toPath());
                } else {
                    Files.writeString(configFile.toPath(), getDefaultBotConfig());
                }
            } catch (Exception e) {
                try {
                    Files.writeString(configFile.toPath(), getDefaultBotConfig());
                } catch (Exception ignored) {

                }
            }
        }

        new File(discordDir, "assets").mkdirs();
        new File(discordDir, "commands").mkdirs();
        new File(discordDir, "buttons").mkdirs();
        new File(discordDir, "flows").mkdirs();
        new File(discordDir, "modals").mkdirs();
        new File(discordDir, "lang").mkdirs();

        // Copy assets
        copyResourceFileIfExists("bots/default/discord/assets/avatar-discordengine-nofon.png",
            new File(discordDir, "assets/avatar-discordengine-nofon.png"));

        // Copy commands
        copyResourceFileIfExists("bots/default/discord/commands/hello.yml", 
            new File(discordDir, "commands/hello.yml"));
        copyResourceFileIfExists("bots/default/discord/commands/register.yml", 
            new File(discordDir, "commands/register.yml"));

        // Copy buttons
        copyResourceFileIfExists("bots/default/discord/buttons/register-button.yml", 
            new File(discordDir, "buttons/register-button.yml"));

        // Copy flows
        copyResourceFileIfExists("bots/default/discord/flows/hello.yml", 
            new File(discordDir, "flows/hello.yml"));
        copyResourceFileIfExists("bots/default/discord/flows/register.yml", 
            new File(discordDir, "flows/register.yml"));

        // Copy modals
        copyResourceFileIfExists("bots/default/discord/modals/user_register.yml", 
            new File(discordDir, "modals/user_register.yml"));

        // Copy language files
        String[] langFiles = {"en_US.yml", "ru_RU.yml", "uk_UA.yml"};
        for (String langFile : langFiles) {
            copyResourceFileIfExists("bots/default/discord/lang/" + langFile, 
                new File(discordDir, "lang/" + langFile));
        }
    }

    private void copyResourceFileIfExists(String resourcePath, File targetFile) {
        try {
            if (!targetFile.exists()) {
                targetFile.getParentFile().mkdirs();

                try (java.io.InputStream is = platform.getResource(resourcePath)) {
                    if (is != null) {
                        Files.copy(is, targetFile.toPath());
                    } else {
                        platform.getLogger().warning("Resource not found: " + resourcePath);
                    }
                }
            }
        } catch (Exception e) {
            platform.getLogger().warning("Could not copy resource file: " + e.getMessage());
        }
    }

    private String getDefaultBotConfig() {
        return """
config:
  version: 1
  type: discord-engine

bot:
  token: "your-bot-token"
  activity:
    text: "Discord Engine"
    type: "PLAYING"
    url: ""
  avatar:
    file: "avatar-discordengine-nofon.png"
""";
    }

    @Override
    public @NotNull Messages getMessages() {
        return messages;
    }
}
