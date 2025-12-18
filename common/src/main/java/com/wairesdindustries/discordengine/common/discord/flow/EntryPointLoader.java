package com.wairesdindustries.discordengine.common.discord.flow;

import java.io.File;
import java.io.IOException;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import com.wairesdindustries.discordengine.common.DiscordEngine;

public class EntryPointLoader {
    private final DiscordEngine api;
    private final EntryPointRegistryImpl entryPointRegistry;

    public EntryPointLoader(DiscordEngine api, EntryPointRegistryImpl entryPointRegistry) {
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
    }

    private void loadBotEntryPoints(File discordFolder) {
        File commandsFolder = new File(discordFolder, "commands");
        loadCommandsFromFolder(commandsFolder);

        File buttonsFolder = new File(discordFolder, "buttons");
        loadButtonsFromFolder(buttonsFolder);
    }

    private void loadCommandsFromFolder(File commandsFolder) {
        if (!commandsFolder.exists() || !commandsFolder.isDirectory()) {
            return;
        }

        File[] commandFiles = commandsFolder.listFiles((dir, name) -> 
            name.endsWith(".yml") || name.endsWith(".yaml"));
        
        if (commandFiles == null) return;

        for (File commandFile : commandFiles) {
            loadCommandFile(commandFile);
        }
    }

    private void loadCommandFile(File commandFile) {
        try {
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .file(commandFile)
                .build();
            
            ConfigurationNode root = loader.load();
            ConfigurationNode commandNode = root.node("command");
            
            if (commandNode.virtual()) {
                return;
            }
            
            String commandId = commandNode.node("id").getString();
            String flowId = commandNode.node("flow").getString();
            String description = commandNode.node("description").getString("Flow-based command");
            String scopeStr = commandNode.node("scope").getString("both");

            if (commandId != null && flowId != null) {
                CommandDefinition.CommandScope scope;
                try {
                    scope = CommandDefinition.CommandScope.valueOf(scopeStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    scope = CommandDefinition.CommandScope.BOTH;
                    api.getPlatform().getLogger().warning("Invalid scope '" + scopeStr + "' for command " + commandId + ", using 'both'");
                }

                CommandDefinition commandDef = new CommandDefinition(commandId, description, flowId, scope);
                entryPointRegistry.registerCommand(commandDef);
                api.getDiscordCommandManager().registerCommand(commandId, description);
            }
            
        } catch (IOException e) {
            api.getPlatform().getLogger().severe("Failed to load command from " + commandFile.getName() + ": " + e.getMessage());
        }
    }

    private void loadButtonsFromFolder(File buttonsFolder) {
        if (!buttonsFolder.exists() || !buttonsFolder.isDirectory()) {
            return;
        }

        File[] buttonFiles = buttonsFolder.listFiles((dir, name) -> 
            name.endsWith(".yml") || name.endsWith(".yaml"));
        
        if (buttonFiles == null) return;

        for (File buttonFile : buttonFiles) {
            loadButtonFile(buttonFile);
        }
    }

    private void loadButtonFile(File buttonFile) {
        try {
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .file(buttonFile)
                .build();
            
            ConfigurationNode root = loader.load();
            ConfigurationNode buttonNode = root.node("button");
            
            if (buttonNode.virtual()) {
                return;
            }
            
            String buttonId = buttonNode.node("id").getString();
            String flowId = buttonNode.node("flow").getString();
            
            if (buttonId != null && flowId != null) {
                entryPointRegistry.registerButton(buttonId, flowId);
            }
            
        } catch (IOException e) {
            api.getPlatform().getLogger().severe("Failed to load button from " + buttonFile.getName() + ": " + e.getMessage());
        }
    }
}