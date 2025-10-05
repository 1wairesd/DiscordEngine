package com.wairesdindustries.discordengine.api.event.plugin;

import com.wairesdindustries.discordengine.api.event.DEEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Accessors(fluent = true)
@Data
public class DiscordEngineReloadEvent extends DEEvent {

    private final Type type;

    /**
     * Enum for reload type
     */
    public enum Type {
        /**
         * Config reloaded
         */
        CONFIG,
        /**
         * Commands reloaded
         */
        COMMAND
    }
}
