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
            
            api.getPlatform().getLogger().info("[DiscordEngine] Loading " + botsList.size() + " bot(s) from configuration...");
            
            for (ConfigurationNode botNode : botsList) {
                String name = botNode.node("name").getString("default");
                boolean enabled = botNode.node("enabled").getBoolean(true);
                
                if (enabled) {
                    createBotStructure(name);
                    DiscordBotService botService = new DiscordBotServiceImpl(api, name);
                    botServices.put(name, botService);
                    api.getPlatform().getLogger().info("[DiscordEngine] Loaded bot configuration: " + name);
                } else {
                    api.getPlatform().getLogger().info("[DiscordEngine] Bot '" + name + "' is disabled in Bots.yml");
                }
            }
        } catch (Exception e) {
            api.getPlatform().getLogger().severe("Failed to load bots configuration: " + e.getMessage());
        }
    }
    
    private void createBotStructure(String botName) {
        File botDir = new File(api.getPlatform().getDataFolder(), "bots/" + botName);
        File discordDir = new File(botDir, "discord");
        File botSubDir = new File(discordDir, "bot");

        File avatarDir = new File(botSubDir, "avatar");
        File commandDir = new File(botSubDir, "command");
        File langDir = new File(botSubDir, "lang");
        
        avatarDir.mkdirs();
        commandDir.mkdirs();
        langDir.mkdirs();

        File configFile = new File(discordDir, "Config.yml");
        if (!configFile.exists()) {
            String globalConfigResource = "bots/default/discord/Config.yml";
            try (java.io.InputStream is = api.getPlatform().getResource(globalConfigResource)) {
                if (is != null) {
                    java.nio.file.Files.copy(is, configFile.toPath());
                } else {
                    java.nio.file.Files.writeString(configFile.toPath(), getDefaultConfig());
                }
            } catch (Exception e) {
                api.getPlatform().getLogger().severe("Failed to create default config for bot: " + botName + " - " + e.getMessage());
                try {
                    java.nio.file.Files.writeString(configFile.toPath(), getDefaultConfig());
                } catch (Exception fallbackException) {
                    api.getPlatform().getLogger().severe("Failed to create fallback default config for bot: " + botName + " - " + fallbackException.getMessage());
                }
            }
        }

        copyDefaultResources(botName, avatarDir, commandDir, langDir);
    }
    
    private void copyDefaultResources(String botName, File avatarDir, File commandDir, File langDir) {
        String defaultAvatarResource = "bots/default/discord/bot/avatar/avatar-discordengine-nofon.png";
        File avatarFile = new File(avatarDir, "avatar-discordengine-nofon.png");
        if (!avatarFile.exists()) {
            try {
                try (java.io.InputStream is = api.getPlatform().getResource(defaultAvatarResource)) {
                    if (is != null) {
                        java.nio.file.Files.copy(is, avatarFile.toPath());
                    }
                }
            } catch (Exception e) {
                api.getPlatform().getLogger().warning("Could not copy default avatar for bot '" + botName + "': " + e.getMessage());
            }
        }

        String defaultCommandResource = "bots/default/discord/bot/command/commands.yml";
        File commandFile = new File(commandDir, "commands.yml");
        if (!commandFile.exists()) {
            try {
                try (java.io.InputStream is = api.getPlatform().getResource(defaultCommandResource)) {
                    if (is != null) {
                        java.nio.file.Files.copy(is, commandFile.toPath());
                    }
                }
            } catch (Exception e) {
                api.getPlatform().getLogger().warning("Could not copy default commands for bot '" + botName + "': " + e.getMessage());
            }
        }

        String[] langFiles = {"en_US.yml", "ru_RU.yml", "uk_UA.yml"};
        for (String langFile : langFiles) {
            String langResource = "bots/default/discord/bot/lang/" + langFile;
            File targetLangFile = new File(langDir, langFile);
            if (!targetLangFile.exists()) {
                try {
                    try (java.io.InputStream is = api.getPlatform().getResource(langResource)) {
                        if (is != null) {
                            java.nio.file.Files.copy(is, targetLangFile.toPath());
                        }
                    }
                } catch (Exception e) {
                    api.getPlatform().getLogger().warning("Could not copy language file " + langFile + " for bot '" + botName + "': " + e.getMessage());
                }
            }
        }
    }
    
    private String getDefaultConfig() {
        return """
config:
  version: 1
  type: discord-config-bot

bot:
  token: "your-bot-token"
  activity:
    text: "Discord Engine"
    type: "PLAYING"
    url: ""

sources:
  commands:
    mode: "both"
    local:
      - "commands.yml"
    global:
      - "commands.yml"
  avatar:
    mode: "local"
    file: "avatar.png"
  lang:
    mode: "local"
    file: "en_US.yml"
""";
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
                    api.getDiscordCommandLoader().load();
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
            api.getPlatform().getLogger().warning("[DiscordEngine] No bots to connect!");
            return;
        }
        
        api.getPlatform().getLogger().info("[DiscordEngine] Connecting " + botServices.size() + " bot(s) to Discord...");
        
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
