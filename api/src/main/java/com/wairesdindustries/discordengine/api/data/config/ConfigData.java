package com.wairesdindustries.discordengine.api.data.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@Accessors(fluent = true)
@Getter
@Setter
@ConfigSerializable
public class ConfigData {

    @Accessors(fluent = true)
    @Getter
    @Setter
    @ConfigSerializable
    public static class Bot {

        @Setting
        private String key = "your-bot-token";

        @Setting
        private String activity = "Discord Engine";
    }

    @Setting
    private String language = "en_US";

    @Setting
    private boolean addonsHelp = true;
}
