package com.wairesdindustries.discordengine.common.config.converter.migrators;

import com.wairesdindustries.discordengine.api.config.Config;
import com.wairesdindustries.discordengine.api.config.converter.ConfigMigrator;
import com.wairesdindustries.discordengine.api.config.converter.ConfigType;
import com.wairesdindustries.discordengine.common.config.converter.DefaultConfigType;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class UnknownMigrator implements ConfigMigrator {

    @Override
    public void migrate(Config config) throws SerializationException {
        String name = config.file().getName().toLowerCase();

        ConfigurationNode node = config.node();
        ConfigType type = config.type();

        if (name.equals("config.yml")) {
            type = DefaultConfigType.CONFIG;
        }

        if (name.equals("bots.yml")) {
            type = DefaultConfigType.DISCORD_BOTS;
        }

        if (config.path().contains("/lang")) type = DefaultConfigType.LANG;

        if (type == DefaultConfigType.UNKNOWN) type = DefaultConfigType.UNKNOWN_CUSTOM;

        node.removeChild("config");
        node.node("config", "version").set(config.version());
        node.node("config", "type").set(type);
        config.type(type);
    }
}
