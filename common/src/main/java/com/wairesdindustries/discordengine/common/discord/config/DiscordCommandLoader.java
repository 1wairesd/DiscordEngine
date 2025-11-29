package com.wairesdindustries.discordengine.common.discord.config;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.spongepowered.configurate.ConfigurationNode;

import com.wairesdindustries.discordengine.api.config.Config;
import com.wairesdindustries.discordengine.api.config.Loadable;
import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.common.DiscordEngine;
import com.wairesdindustries.discordengine.common.config.converter.DefaultConfigType;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordAddRoleAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordBanAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordButtonAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordChannelMessageAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordCreateChannelAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordCreateRoleAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordDeafenAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordDeleteMessagesAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordEmbedAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordEphemeralMessageAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordKickAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordLockChannelAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordModalAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordMoveToVoiceAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordMuteAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordNicknameAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordPinMessageAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordPrivateMessageAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordReactionAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordRemoveRoleAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordSelectMenuAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordSendMessageAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordSlowmodeAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordThreadAction;
import com.wairesdindustries.discordengine.common.discord.entities.actions.DiscordTimeoutAction;
import com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandImpl;

public class DiscordCommandLoader implements Loadable {
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
                    DiscordAction action = parseAction(actionNode);
                    if (action != null) {
                        actions.add(action);
                    }
                }

                String trigger = cmdNode.node("trigger").getString(name);

                api.getDiscordCommandManager().registerCommand(
                        new DiscordCommandImpl(name, trigger, description, actions)
                );
            }
        }
    }

    private DiscordAction parseAction(ConfigurationNode actionNode) {
        Object typeKey = actionNode.childrenMap().keySet().iterator().next();
        String type = typeKey.toString();
        ConfigurationNode params = actionNode.node(type);

        switch (type) {
            case "send_message":
                return new DiscordSendMessageAction(params.node("content").getString(""));
                
            case "ephemeral_message":
                return new DiscordEphemeralMessageAction(params.node("content").getString(""));
                
            case "embed":
                return parseEmbedAction(params);
                
            case "add_role":
                return new DiscordAddRoleAction(
                    params.node("role_id").getString(""),
                    params.node("target_option").getString(null)
                );
                
            case "remove_role":
                return new DiscordRemoveRoleAction(
                    params.node("role_id").getString(""),
                    params.node("target_option").getString(null)
                );
                
            case "reaction":
                return new DiscordReactionAction(params.node("emoji").getString(""));
                
            case "private_message":
                return new DiscordPrivateMessageAction(params.node("content").getString(""));
                
            case "kick":
                return new DiscordKickAction(
                    params.node("target_option").getString("user"),
                    params.node("reason").getString(null)
                );
                
            case "ban":
                return new DiscordBanAction(
                    params.node("target_option").getString("user"),
                    params.node("reason").getString(null),
                    params.node("delete_message_days").getInt(0)
                );
                
            case "timeout":
                return new DiscordTimeoutAction(
                    params.node("target_option").getString("user"),
                    params.node("duration_minutes").getLong(5),
                    params.node("reason").getString(null)
                );
                
            case "channel_message":
                return new DiscordChannelMessageAction(
                    params.node("channel_id").getString(""),
                    params.node("content").getString("")
                );
                
            case "create_channel":
                return new DiscordCreateChannelAction(
                    params.node("name").getString("new-channel"),
                    params.node("type").getString("TEXT")
                );
                
            case "create_role":
                return new DiscordCreateRoleAction(
                    params.node("name").getString("New Role"),
                    params.node("color").getString(null),
                    params.node("hoisted").getBoolean(false),
                    params.node("mentionable").getBoolean(false)
                );

            case "move_to_voice":
                return new DiscordMoveToVoiceAction(
                        params.node("target_option").getString("user"),
                        params.node("voice_channel_id").getString("")
                );

            case "mute":
                return new DiscordMuteAction(
                        params.node("target_option").getString("user"),
                        params.node("mute").getBoolean(true)
                );

            case "deafen":
                return new DiscordDeafenAction(
                        params.node("target_option").getString("user"),
                        params.node("deafen").getBoolean(true)
                );

            case "nickname":
                return new DiscordNicknameAction(
                        params.node("target_option").getString(null),
                        params.node("nickname").getString(null)
                );

            case "pin_message":
                return new DiscordPinMessageAction(params.node("pin").getBoolean(true));

            case "create_thread":
                return new DiscordThreadAction(
                        params.node("name").getString("New Thread"),
                        params.node("private").getBoolean(false)
                );
                
            case "delete_messages":
                return new DiscordDeleteMessagesAction(params.node("amount").getInt(10));
                
            case "slowmode":
                return new DiscordSlowmodeAction(params.node("seconds").getInt(0));
                
            case "lock_channel":
                return new DiscordLockChannelAction(params.node("lock").getBoolean(true));
                
            case "buttons":
                return parseButtonAction(params);
                
            case "select_menu":
                return parseSelectMenuAction(params);
                
            case "modal":
                return parseModalAction(params);
                
            default:
                return null;
        }
    }

    private DiscordAction parseButtonAction(ConfigurationNode params) {
        String message = params.node("message").virtual() ? null : params.node("message").getString();
        List<Map<String, String>> buttons = new ArrayList<>();
        
        ConfigurationNode buttonsNode = params.node("buttons");
        if (!buttonsNode.virtual()) {
            for (ConfigurationNode buttonNode : buttonsNode.childrenList()) {
                Map<String, String> button = new HashMap<>();
                button.put("label", buttonNode.node("label").getString("Button"));
                button.put("id", buttonNode.node("id").virtual() ? null : buttonNode.node("id").getString());
                button.put("style", buttonNode.node("style").getString("PRIMARY"));
                button.put("emoji", buttonNode.node("emoji").virtual() ? null : buttonNode.node("emoji").getString());
                button.put("url", buttonNode.node("url").virtual() ? null : buttonNode.node("url").getString());
                buttons.add(button);
            }
        }
        
        return new DiscordButtonAction(message, buttons);
    }

    private DiscordAction parseSelectMenuAction(ConfigurationNode params) {
        String message = params.node("message").virtual() ? null : params.node("message").getString();
        String menuId = params.node("id").virtual() ? null : params.node("id").getString();
        String placeholder = params.node("placeholder").virtual() ? null : params.node("placeholder").getString();
        List<Map<String, String>> options = new ArrayList<>();
        
        ConfigurationNode optionsNode = params.node("options");
        if (!optionsNode.virtual()) {
            for (ConfigurationNode optionNode : optionsNode.childrenList()) {
                Map<String, String> option = new HashMap<>();
                option.put("label", optionNode.node("label").getString("Option"));
                option.put("value", optionNode.node("value").virtual() ? null : optionNode.node("value").getString());
                option.put("description", optionNode.node("description").virtual() ? null : optionNode.node("description").getString());
                option.put("emoji", optionNode.node("emoji").virtual() ? null : optionNode.node("emoji").getString());
                options.add(option);
            }
        }
        
        return new DiscordSelectMenuAction(message, menuId, placeholder, options);
    }

    private DiscordAction parseModalAction(ConfigurationNode params) {
        String modalId = params.node("id").virtual() ? null : params.node("id").getString();
        String title = params.node("title").virtual() ? null : params.node("title").getString();
        List<Map<String, String>> inputs = new ArrayList<>();
        
        ConfigurationNode inputsNode = params.node("inputs");
        if (!inputsNode.virtual()) {
            for (ConfigurationNode inputNode : inputsNode.childrenList()) {
                Map<String, String> input = new HashMap<>();
                input.put("id", inputNode.node("id").virtual() ? null : inputNode.node("id").getString());
                input.put("label", inputNode.node("label").getString("Input"));
                input.put("style", inputNode.node("style").getString("SHORT"));
                input.put("placeholder", inputNode.node("placeholder").virtual() ? null : inputNode.node("placeholder").getString());
                input.put("value", inputNode.node("value").virtual() ? null : inputNode.node("value").getString());
                input.put("required", String.valueOf(inputNode.node("required").getBoolean(true)));
                inputs.add(input);
            }
        }
        
        return new DiscordModalAction(modalId, title, inputs);
    }

    private DiscordAction parseEmbedAction(ConfigurationNode params) {
        List<Map<String, String>> fields = new ArrayList<>();
        ConfigurationNode fieldsNode = params.node("fields");
        
        if (!fieldsNode.virtual()) {
            for (ConfigurationNode fieldNode : fieldsNode.childrenList()) {
                Map<String, String> field = new HashMap<>();
                field.put("name", fieldNode.node("name").getString("Field"));
                field.put("value", fieldNode.node("value").getString(""));
                field.put("inline", String.valueOf(fieldNode.node("inline").getBoolean(false)));
                fields.add(field);
            }
        }

        return new DiscordEmbedAction(
            params.node("title").virtual() ? null : params.node("title").getString(),
            params.node("description").virtual() ? null : params.node("description").getString(),
            params.node("color").virtual() ? null : params.node("color").getString(),
            params.node("thumbnail").virtual() ? null : params.node("thumbnail").getString(),
            params.node("image").virtual() ? null : params.node("image").getString(),
            params.node("footer").virtual() ? null : params.node("footer").getString(),
            params.node("author").virtual() ? null : params.node("author").getString(),
            fields
        );
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
