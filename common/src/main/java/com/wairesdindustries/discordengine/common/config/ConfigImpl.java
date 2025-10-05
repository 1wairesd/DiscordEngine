package com.wairesdindustries.discordengine.common.config;

import com.wairesdindustries.discordengine.api.config.Config;
import com.wairesdindustries.discordengine.api.config.converter.ConfigType;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;

public class ConfigImpl implements Config {

    private final File file;
    private final YamlConfigurationLoader loader;
    private ConfigurationNode node;
    private ConfigType type = DefaultConfigType.DEFAULT;
    private int version = 1;
    private Object serialized;

    public ConfigImpl(File file) {
        this.file = file;
        this.loader = YamlConfigurationLoader.builder().file(file).build();
    }

    @Override
    public <T> T getSerialized(Class<T> clazz) {
        if (serialized != null && clazz.isInstance(serialized)) return clazz.cast(serialized);

        try {
            T obj = node.get(clazz);
            serialized = obj;
            return obj;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize config: " + file.getName(), e);
        }
    }


    @Override
    public ConfigurationNode node() {
        return node;
    }

    @Override
    public File file() {
        return file;
    }

    @Override
    public String path() {
        return file.getPath().replace("\\", "/");
    }

    @Override
    public int version() {
        return version;
    }

    @Override
    public ConfigType type() {
        return type;
    }

    @Override
    public void type(ConfigType type) {
        this.type = type;
    }

    @Override
    public void load() throws ConfigurateException {
        try {
            node = loader.load();
        } catch (IOException e) {
            throw new ConfigurateException("Failed to load config: " + file.getName(), e);
        }
    }

    @Override
    public boolean delete() {
        return file.delete();
    }

    @Override
    public void save() throws ConfigurateException {
        try {
            loader.save(node);
        } catch (IOException e) {
            throw new ConfigurateException("Failed to save config: " + file.getName(), e);
        }
    }
}
