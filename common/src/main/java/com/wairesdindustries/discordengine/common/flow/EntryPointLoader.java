package com.wairesdindustries.discordengine.common.flow;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import com.wairesdindustries.discordengine.common.DiscordEngine;

public class EntryPointLoader {
    private final DiscordEngine api;
    private final EntryPointRegistry entryPointRegistry;

    public EntryPointLoader(DiscordEngine api, EntryPointRegistry entryPointRegistry) {
        this.api = api;
        this.entryPointRegistry = entryPointRegistry;
    }

    public void load() {
        entryPointRegistry.clear();

        File dataFolder = api.getPlatform().getDataFolder();
        File botsFolder = new File(dataFolder, "bots");
        
        if (!botsFolder.exists() || !botsFolder.isDirectory()) {
            return;
        }

        File[] botDirs = botsFolder.listFiles(File::isDirectory);
        if (botDirs == null) return;

        for (File botDir : botDirs) {
            File discordFolder = new File(botDir, "discord");
            if (discordFolder.exists()) {
                loadBotEntryPoints(discordFolder);
            }
        }

        int commandCount = entryPointRegistry.getAllCommands().size();
        int buttonCount = entryPointRegistry.getAllButtons().size();
        api.getPlatform().getLogger().info("[FlowEngine] Loaded " + commandCount + " command mappings and " + buttonCount + " button mappings");
        
        if (commandCount > 0) {
            api.getPlatform().getLogger().info("[FlowEngine] Available commands: " + String.join(", ", entryPointRegistry.getAllCommands().keySet()));
        }
    }

    private void loadBotEntryPoints(File discordFolder) {
        File globalsFolder = new File(discordFolder, "globals");
        api.getPlatform().getLogger().info("[FlowEngine] Looking for globals in: " + globalsFolder.getAbsolutePath());
        
        if (!globalsFolder.exists() || !globalsFolder.isDirectory()) {
            api.getPlatform().getLogger().warning("[FlowEngine] Globals folder not found: " + globalsFolder.getAbsolutePath());
            return;
        }

        loadCommands(new File(globalsFolder, "commands.yml"));
        loadButtons(new File(globalsFolder, "buttons.yml"));
    }

    private void loadCommands(File commandsFile) {
        api.getPlatform().getLogger().info("[FlowEngine] Loading commands from: " + commandsFile.getAbsolutePath());
        
        if (!commandsFile.exists()) {
            api.getPlatform().getLogger().warning("[FlowEngine] Commands file not found: " + commandsFile.getAbsolutePath());
            return;
        }

        try {
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .file(commandsFile)
                .build();
            
            ConfigurationNode root = loader.load();
            ConfigurationNode commandsNode = root.node("commands");
            
            api.getPlatform().getLogger().info("[FlowEngine] Commands node virtual: " + commandsNode.virtual());
            api.getPlatform().getLogger().info("[FlowEngine] Commands node children count: " + commandsNode.childrenMap().size());
            
            if (!commandsNode.virtual()) {
                for (Map.Entry<Object, ? extends ConfigurationNode> entry : commandsNode.childrenMap().entrySet()) {
                    String commandName = entry.getKey().toString();
                    ConfigurationNode commandNode = entry.getValue();
                    
                    String flowId = commandNode.node("flow").getString();
                    String description = commandNode.node("description").getString("Flow-based command");
                    
                    api.getPlatform().getLogger().info("[FlowEngine] Processing command: " + commandName + " -> flow: " + flowId);
                    
                    if (flowId != null) {
                        entryPointRegistry.registerCommand(commandName, flowId);
                        api.getDiscordCommandManager().registerCommand(commandName, description);
                        api.getPlatform().getLogger().info("[FlowEngine] Registered command: " + commandName + " -> " + flowId);
                    }
                }
            }
            
        } catch (IOException e) {
            api.getPlatform().getLogger().severe("Failed to load commands from " + commandsFile.getName() + ": " + e.getMessage());
        }
    }

    private void loadButtons(File buttonsFile) {
        if (!buttonsFile.exists()) {
            return;
        }

        try {
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .file(buttonsFile)
                .build();
            
            ConfigurationNode root = loader.load();
            ConfigurationNode buttonsNode = root.node("buttons");
            
            if (!buttonsNode.virtual()) {
                for (Map.Entry<Object, ? extends ConfigurationNode> entry : buttonsNode.childrenMap().entrySet()) {
                    String buttonId = entry.getKey().toString();
                    ConfigurationNode buttonNode = entry.getValue();
                    
                    String flowId = buttonNode.node("flow").getString();
                    if (flowId != null) {
                        entryPointRegistry.registerButton(buttonId, flowId);
                    }
                }
            }
            
        } catch (IOException e) {
            api.getPlatform().getLogger().severe("Failed to load buttons from " + buttonsFile.getName() + ": " + e.getMessage());
        }
    }
}