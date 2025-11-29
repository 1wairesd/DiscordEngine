package com.wairesdindustries.discordengine.common.discord.config;

import com.wairesdindustries.discordengine.api.config.Config;
import com.wairesdindustries.discordengine.api.config.Loadable;
import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.common.DiscordEngine;
import com.wairesdindustries.discordengine.common.config.converter.DefaultConfigType;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordSendMessageAction;
import com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandImpl;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DiscordCommandLoader implements Loadable {
    private static final String DEFAULT_COMMAND_FILE = "commands.yml";
    private static final List<String> DEFAULT_COMMANDS = List.of();

    private final DiscordEngine api;

    public DiscordCommandLoader(DiscordEngine api) {
        this.api = api;
    }

    @Override
    public void load() {
        List<Config> commandConfigs = api.getConfigManager().get().values().stream()
            .filter(config -> config.type() == DefaultConfigType.DISCORD_COMMAND_BOT)
            .collect(Collectors.toList());

        if (commandConfigs.isEmpty()) {
            saveDefault();
            api.getConfigManager().load();
            commandConfigs = api.getConfigManager().get().values().stream()
                .filter(config -> config.type() == DefaultConfigType.DISCORD_COMMAND_BOT)
                .collect(Collectors.toList());
        }

        for (Config config : commandConfigs) {
            ConfigurationNode root = config.node();
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
        }
    }

    private void saveDefault() {
        for (String resource : DEFAULT_COMMANDS) {
            File out = new File(api.getPlatform().getDataFolder(), resource);
            if (!out.exists()) {
                if (api.getPlatform().getResource(resource) != null) {
                    out.getParentFile().mkdirs();
                    api.getPlatform().saveResource(resource, false);
                }
            }
        }
    }
}
