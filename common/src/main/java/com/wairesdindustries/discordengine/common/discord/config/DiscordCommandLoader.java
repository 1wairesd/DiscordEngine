package com.wairesdindustries.discordengine.common.discord.config;

import com.wairesdindustries.discordengine.api.config.Loadable;
import com.wairesdindustries.discordengine.api.discord.actions.DiscordAction;
import com.wairesdindustries.discordengine.common.DiscordEngine;
import com.wairesdindustries.discordengine.common.discord.actions.DiscordSendMessageAction;
import com.wairesdindustries.discordengine.common.discord.commands.DiscordCommandImpl;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DiscordCommandLoader implements Loadable {
    private static final String COMMANDS_FOLDER = "commands";
    private static final String DEFAULT_COMMAND_FILE = "commands.yml";
    private static final List<String> DEFAULT_COMMANDS = List.of(COMMANDS_FOLDER + "/" + DEFAULT_COMMAND_FILE);

    private final DiscordEngine api;

    public DiscordCommandLoader(DiscordEngine api) {
        this.api = api;
    }

    @Override
    public void load() {
        File folder = new File(api.getPlatform().getDataFolder(), COMMANDS_FOLDER);
        if (!folder.exists()) folder.mkdirs();

        List<File> yamlFiles = new ArrayList<>();
        collectYamlFiles(folder, yamlFiles);

        if (yamlFiles.isEmpty()) {
            saveDefault();
            collectYamlFiles(folder, yamlFiles);
        }

        for (File file : yamlFiles) {
            try {
                YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                        .file(file)
                        .build();
                ConfigurationNode root = loader.load();

                ConfigurationNode commandsNode = root.node("commands");
                for (Object key : commandsNode.childrenMap().keySet()) {
                    String name = key.toString();
                    ConfigurationNode cmdNode = commandsNode.node(name);
                    String description = cmdNode.node("description").getString("");

                    List<DiscordAction> actions = new ArrayList<>();
                    for (ConfigurationNode actionNode : cmdNode.node("actions").childrenList()) {
                        Object typeKey = actionNode.childrenMap().keySet().iterator().next();
                        String type = typeKey.toString();
                        ConfigurationNode params = actionNode.node(type);

                        if ("send_message".equals(type)) {
                            actions.add(new DiscordSendMessageAction(params.node("content").getString("")));
                        }
                    }

                    String trigger = cmdNode.node("trigger").getString(name);

                    api.getDiscordCommandManager().registerCommand(
                            new DiscordCommandImpl(name, trigger, description, actions)
                    );
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void collectYamlFiles(File folder, List<File> out) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                collectYamlFiles(file, out);
            } else if (file.getName().endsWith(".yml")) {
                out.add(file);
            }
        }
    }

    private void saveDefault() {
        for (String resource : DEFAULT_COMMANDS) {
            File out = new File(api.getPlatform().getDataFolder(), resource);
            if (!out.exists()) {
                out.getParentFile().mkdirs();
                api.getPlatform().saveResource(resource, false);
            }
        }
    }
}
