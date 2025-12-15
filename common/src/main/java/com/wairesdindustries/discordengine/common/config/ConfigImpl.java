package com.wairesdindustries.discordengine.common.config;

import java.io.File;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import com.wairesdindustries.discordengine.api.config.Config;
import com.wairesdindustries.discordengine.api.config.converter.ConfigType;
import com.wairesdindustries.discordengine.api.data.config.ConfigSerializer;
import com.wairesdindustries.discordengine.common.config.converter.DefaultConfigType;

public class ConfigImpl implements Config {
    private final File file;
    private final YamlConfigurationLoader loader;
    private ConfigurationNode node;
    private ConfigType type;
    private int version = 1;
    private Object serialized;
    private boolean deleted;

    public ConfigImpl(File file) {
        this(file, null);
    }

    public ConfigImpl(File file, ConfigType type) {
        this.file = file;
        this.type = type;
        this.loader = YamlConfigurationLoader.builder()
            .file(file)
            .nodeStyle(NodeStyle.BLOCK)
            .defaultOptions(opts -> opts.serializers(builder -> builder.registerAnnotatedObjects(ObjectMapper.factory())))
            .build();
    }

    private void setMeta() throws SerializationException {
        ConfigurationNode metaNode = node.node("config");
        String version = metaNode.node("version").getString();
        String typeString = metaNode.node("type").getString();

        if (version != null) {
            this.version = parse(version);
            if (this.type == null) {
                this.type = DefaultConfigType.getType(typeString);
            }
        } else {
            if (this.type == null) {
                this.type = DefaultConfigType.UNKNOWN;
            }
        }

        if (type != null) {
            ConfigSerializer configSerializer = type.getConfigSerializer();
            if (configSerializer != null) {
                this.serialized = node(configSerializer.path()).get(configSerializer.serializer());
            }
        }
    }

    @Override
    public void load() throws ConfigurateException {
        node = loader.load();
        setMeta();
    }

    private int parse(String string) {
        if (string == null) return 0;
        if (string.contains(".")) string = string.replace(".", "");
        return Integer.parseInt(string);
    }

    @Override
    public @Nullable <T> T getSerialized(Class<T> clazz) {
        return clazz.cast(serialized);
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
    public boolean delete() {
        deleted = file.delete();
        return deleted;
    }

    @Override
    public void save() throws ConfigurateException {
        if (!deleted) {
            loader.save(node);
        }
    }
}
