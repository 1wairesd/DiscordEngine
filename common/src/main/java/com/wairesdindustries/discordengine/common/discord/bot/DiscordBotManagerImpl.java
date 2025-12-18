package com.wairesdindustries.discordengine.common.discord.bot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.configurate.ConfigurationNode;

import com.wairesdindustries.discordengine.api.discord.bot.DiscordBotManager;
import com.wairesdindustries.discordengine.api.discord.bot.DiscordBotService;
import com.wairesdindustries.discordengine.common.DiscordEngine;

public class DiscordBotManagerImpl implements DiscordBotManager {
    private final DiscordEngine api;
    private final Map<String, DiscordBotService> botServices;
    
    public DiscordBotManagerImpl(DiscordEngine api) {
        this.api = api;
        this.botServices = new ConcurrentHashMap<>();
    }
    
    @Override
    public void loadBots() {
        try {
            ConfigurationNode botsConfig = api.getConfigManager().getConfig("Bots.yml").node();
            List<? extends ConfigurationNode> botsList = botsConfig.node("bots").childrenList();
            
            for (ConfigurationNode botNode : botsList) {
                String name = botNode.node("name").getString("default");
                boolean enabled = botNode.node("enabled").getBoolean(true);
                
                if (enabled) {
                    createBotStructure(name);
                    DiscordBotService botService = new DiscordBotServiceImpl(api, name);
                    botServices.put(name, botService);
                }
            }
        } catch (Exception e) {
            api.getPlatform().getLogger().severe("Failed to load bots configuration: " + e.getMessage());
        }
    }
    
    private void createBotStructure(String botName) {
        File botDir = new File(api.getPlatform().getDataFolder(), "bots/" + botName);
        boolean isNewBot = !botDir.exists();
        
        if (isNewBot) {
            botDir.mkdirs();
        }

        if (api.getConfigManager() instanceof com.wairesdindustries.discordengine.common.config.ConfigManagerImpl configManager) {
            configManager.copyBotDefaultResources(botName);
        }
    }
    
    @Override
    public DiscordBotService getBot(String name) {
        return botServices.get(name);
    }
    
    @Override
    public CompletableFuture<Void> reloadBot(String name, String type) {
        return CompletableFuture.runAsync(() -> {
            DiscordBotService bot = botServices.get(name);
            if (bot == null) {
                api.getPlatform().getLogger().warning("Bot not found: " + name);
                return;
            }
            
            switch (type.toLowerCase()) {
                case "activity" -> {
                    api.getConfigManager().load();
                    api.getDiscordAvatarService().updateActivity("Discord Engine");
                }
                case "avatar" -> {
                    api.getConfigManager().load();
                    api.getDiscordAvatarService().updateAvatar();
                }
                case "command" -> {
                    api.getFlowManager().load();
                    api.getDiscordCommandManager().registerAll();
                }
                case "lang" -> api.getConfigManager().load();
                case "restart" -> {
                    bot.close();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    bot.connect().join();
                }
                default -> api.getPlatform().getLogger().warning("Unknown reload type: " + type);
            }
        });
    }
    
    @Override
    public void connectAll() {
        if (botServices.isEmpty()) {
            api.getPlatform().getLogger().warning("No bots to connect!");
            return;
        }
        
        api.getPlatform().getLogger().info("Connecting " + botServices.size() + " bot(s) to Discord...");
        
        List<CompletableFuture<Void>> futures = botServices.values().stream()
                .map(DiscordBotService::connect)
                .toList();
        
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
    
    @Override
    public void closeAll() {
        botServices.values().forEach(DiscordBotService::close);
    }
    
    @Override
    public List<String> getBotNames() {
        return new ArrayList<>(botServices.keySet());
    }
}
