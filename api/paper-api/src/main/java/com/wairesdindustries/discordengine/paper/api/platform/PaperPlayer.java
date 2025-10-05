package com.wairesdindustries.discordengine.paper.api.platform;

import com.wairesdindustries.discordengine.api.platform.DEPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class PaperPlayer extends PaperCommandSender implements DEPlayer {

    private final Player player;

    public PaperPlayer(@NotNull Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public @NotNull Player getHandler() {
        return player;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public void openInventory(Object inventory) {
        player.openInventory((Inventory) inventory);
    }

    @Override
    public void closeInventory() {
        player.closeInventory();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        PaperPlayer that = (PaperPlayer) object;
        return Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), player);
    }
}
