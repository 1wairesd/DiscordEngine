package com.wairesdindustries.discordengine.api.platform;

import org.jetbrains.annotations.NotNull;

public interface DEPlayer extends DECommandSender, DEOfflinePlayer {

    @NotNull String getName();

    void openInventory(Object inventory);

    void closeInventory();

}