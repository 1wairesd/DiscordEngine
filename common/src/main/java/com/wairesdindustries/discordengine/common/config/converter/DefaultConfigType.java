package com.wairesdindustries.discordengine.common.config.converter;

import com.wairesdindustries.discordengine.api.config.converter.ConfigMigrator;
import com.wairesdindustries.discordengine.api.config.converter.ConfigType;
import com.wairesdindustries.discordengine.api.data.config.ConfigData;
import com.wairesdindustries.discordengine.api.data.config.ConfigSerializer;
import com.wairesdindustries.discordengine.common.config.converter.migrators.UnknownMigrator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum DefaultConfigType implements ConfigType {

    CONFIG(1,
            new HashMap<Integer, ConfigMigrator>() {{
                // миграторы будут добавлены позже
            }},
            new ConfigSerializer(ConfigData.class)
    ),

    UNKNOWN_CUSTOM(0),

    LANG(1, new HashMap<Integer, ConfigMigrator>() {{
        // миграторы будут добавлены позже
    }}),

    UNKNOWN(true, new UnknownMigrator());

    private int latestVersion;
    private boolean permanent;
    private ConfigMigrator permanentMigrator;
    private ConfigSerializer configSerializer;

    private Map<Integer, ConfigMigrator> migrations;

    @NotNull
    public static DefaultConfigType getType(String name) {
        if (name != null) {
            try {
                return valueOf(name.toUpperCase());
            } catch (IllegalArgumentException ex) {
                return UNKNOWN_CUSTOM;
            }
        }
        return UNKNOWN;
    }

    DefaultConfigType(int latestVersion) {
        this.latestVersion = latestVersion;
    }

    DefaultConfigType(int latestVersion, ConfigSerializer configSerializer) {
        this(latestVersion);
        this.configSerializer = configSerializer;
    }

    DefaultConfigType(int latestVersion, Map<Integer, ConfigMigrator> migrations) {
        this(latestVersion);
        this.migrations = migrations;
    }

    DefaultConfigType(int latestVersion, Map<Integer, ConfigMigrator> migrations, ConfigSerializer configSerializer) {
        this(latestVersion, migrations);
        this.configSerializer = configSerializer;
    }

    DefaultConfigType(boolean permanent, ConfigMigrator permanentMigrator) {
        this.permanent = permanent;
        this.permanentMigrator = permanentMigrator;
    }

    @Override
    public String getName() {
        return toString();
    }

    @Override
    public int getLatestVersion() {
        return latestVersion;
    }

    @Override
    public boolean isPermanent() {
        return permanent;
    }

    @Override
    public ConfigMigrator getMigrator(int version) {
        if (migrations == null) return permanentMigrator;
        return migrations.get(version);
    }

    @Override
    public @Nullable ConfigSerializer getConfigSerializer() {
        return configSerializer;
    }

    public boolean isUnknown() {
        return this == UNKNOWN || this == UNKNOWN_CUSTOM;
    }
}
