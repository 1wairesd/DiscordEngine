package com.wairesdindustries.discordengine.api.platform;

import org.jetbrains.annotations.NotNull;

public interface DECommandSender {

    @NotNull String getName();

    @NotNull Object getHandler();

	boolean hasPermission(String permission);

	void sendMessage(@NotNull String message);

	boolean isConsole();
}
