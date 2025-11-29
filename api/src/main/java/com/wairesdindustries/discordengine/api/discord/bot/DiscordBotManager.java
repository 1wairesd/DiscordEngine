package com.wairesdindustries.discordengine.api.discord.bot;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DiscordBotManager {
    void loadBots();
    DiscordBotService getBot(String name);
    CompletableFuture<Void> reloadBot(String name, String type);
    void connectAll();
    void closeAll();
    List<String> getBotNames();
}
