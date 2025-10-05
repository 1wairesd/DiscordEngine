package com.wairesdindustries.discordengine.api.config.converter;

import com.wairesdindustries.discordengine.api.data.config.ConfigSerializer;
import org.jetbrains.annotations.Nullable;

public interface ConfigType {

    String getName();

    int getLatestVersion();

    boolean isPermanent();

    ConfigMigrator getMigrator(int version);

    @Nullable
    default ConfigSerializer getConfigSerializer() {
        return null;
    }

}
