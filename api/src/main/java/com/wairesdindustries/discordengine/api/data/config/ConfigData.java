package com.wairesdindustries.discordengine.api.data.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
@Accessors(fluent = true)
@Getter
@Setter
public class ConfigData {

    @Setting
    private Bot bot = new Bot();

    @Setting
    private String language = "en_US";

    @Setting
    private boolean addonsHelp = true;

    @ConfigSerializable
    @Accessors(fluent = true)
    @Getter
    @Setter
    public static class Bot {
        @Setting
        private String token = "your-bot-token";

        @Setting
        private String activity = "Discord Engine";

        @Setting
        private String avatar = "avatar-discordengine-nofon.png";
    }
}