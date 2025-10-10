package com.wairesdindustries.discordengine.paper.api.platform;

import com.wairesdindustries.discordengine.api.platform.DEOfflinePlayer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PaperOfflinePlayer implements DEOfflinePlayer {

    private final OfflinePlayer player;

    public PaperOfflinePlayer(OfflinePlayer player) {
        this.player = player;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public @NotNull OfflinePlayer getHandler() {
        return player;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return player.getUniqueId();
    }
}
