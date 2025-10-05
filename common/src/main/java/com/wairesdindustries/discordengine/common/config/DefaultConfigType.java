package com.wairesdindustries.discordengine.common.config;

import com.wairesdindustries.discordengine.api.config.converter.ConfigMigrator;
import com.wairesdindustries.discordengine.api.config.converter.ConfigType;
import com.wairesdindustries.discordengine.api.data.config.ConfigSerializer;
import org.jetbrains.annotations.Nullable;

public enum DefaultConfigType implements ConfigType {
    DEFAULT("default", 1, true);

    private final String name;
    private final int latestVersion;
    private final boolean permanent;

    DefaultConfigType(String name, int latestVersion, boolean permanent) {
        this.name = name;
        this.latestVersion = latestVersion;
        this.permanent = permanent;
    }

    @Override
    public String getName() {
        return name;
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
        return null;
    }

    @Override
    public @Nullable ConfigSerializer getConfigSerializer() {
        return null;
    }
}
