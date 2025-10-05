package com.wairesdindustries.discordengine.common.command;

import com.wairesdindustries.discordengine.BuildConstants;
import com.wairesdindustries.discordengine.api.addon.Addon;
import com.wairesdindustries.discordengine.api.platform.DECommandSender;
import com.wairesdindustries.discordengine.api.data.subcommand.SubCommand;
import com.wairesdindustries.discordengine.api.data.subcommand.SubCommandExecutor;
import com.wairesdindustries.discordengine.api.data.subcommand.SubCommandTabCompleter;
import com.wairesdindustries.discordengine.api.tools.DETools;
import com.wairesdindustries.discordengine.common.platform.BackendPlatform;
import com.wairesdindustries.discordengine.common.tools.LocalPlaceholder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GlobalCommand implements SubCommandExecutor, SubCommandTabCompleter {

	private final BackendPlatform backend;

	public GlobalCommand(BackendPlatform backend) {
		this.backend = backend;
	}

    @Override
    public boolean execute(@NotNull DECommandSender sender, @NotNull String label, String @NotNull [] args) {
        if (args.length == 0) {
            sendHelp(sender, label);
        } else {
            String subCommandName = args[0];
            SubCommand subCommand = backend.getAPI().getSubCommandManager().get(subCommandName);

            if (subCommand == null) {
                sendHelp(sender, label);
                return false;
            }

            String permission = subCommand.permission();

            if (permission == null || sender.hasPermission(permission)) {
                try {
                    if (!subCommand.execute(sender, label, Arrays.copyOfRange(args, 1, args.length))) sendHelp(sender, label);
                } catch (Exception e) {
                    backend.getLogger().log(Level.WARNING, "Error with executing subcommand: " + subCommandName, e);
                }
            } else {
                sender.sendMessage(DETools.prefix(backend.getAPI().getConfigManager().getMessages().getString("no-permission")));
            }
        }

        return true;
    }

    public void sendHelp(DECommandSender sender, String label) {
        if (!sender.hasPermission("discordengine.player")) {
            sender.sendMessage(DETools.prefix(backend.getAPI().getConfigManager().getMessages().getString("no-permission")));
            return;
        }

        sender.sendMessage(DETools.rc("&aDiscordEngine &7v&6" + backend.getVersion() + " &7(&eAPI &7v&6" + BuildConstants.api + "&7) by &c_1wairesd__"));

        if (!sender.hasPermission("discordengine.moder")) {
            sendHelpMessages(sender, "help-player", label);
        } else {
            sendHelpMessages(sender, "help", label);
        }

        if (backend.getAPI().getConfigManager().getConfig().addonsHelp()) {
            Map<String, List<Map<String, SubCommand>>> addonsMap = buildAddonsMap();
            if (DETools.isHasCommandForSender(sender, addonsMap)) {
                sendAddonHelpMessages(sender, addonsMap);
            }
        }
    }

    private void sendHelpMessages(DECommandSender sender, String path, String label) {
        for (String string : backend.getAPI().getConfigManager().getMessages().getStringList(path)) {
            sender.sendMessage(
                    DETools.rc(
                            DETools.rt(string, LocalPlaceholder.of("%cmd%", label))
                    )
            );
        }
    }

    private Map<String, List<Map<String, SubCommand>>> buildAddonsMap() {
        Map<String, List<Map<String, SubCommand>>> addonsMap = new HashMap<>();
        backend.getAPI().getSubCommandManager().getMap().forEach((subCommandName, subCommand) -> {
            Addon addon = subCommand.addon();
            addonsMap.computeIfAbsent(addon.getName(), k -> new ArrayList<>())
                    .add(Collections.singletonMap(subCommandName, subCommand));
        });
        return addonsMap;
    }

    private void sendAddonHelpMessages(DECommandSender sender, Map<String, List<Map<String, SubCommand>>> addonsMap) {
        addonsMap.forEach((addon, commands) -> {
            if (!addon.equalsIgnoreCase("DiscordEngine") && DETools.isHasCommandForSender(sender, commands)) {
                String addonNameFormat = backend.getAPI().getConfigManager().getMessages().getString("help-addons", "format", "name");
                if (!addonNameFormat.isEmpty()) {
                    sender.sendMessage(
                            DETools.rc(
                                    DETools.rt(addonNameFormat, LocalPlaceholder.of("%addon%", addon))
                            )
                    );
                }

                commands.forEach(command -> command.forEach((commandName, subCommand) -> {
                    String description = subCommand.description();
                    description = (description != null) ? DETools.rt(
                            backend.getAPI().getConfigManager().getMessages().getString("help-addons", "format", "description"),
                            LocalPlaceholder.of("%description%", description)) : "";

                    StringBuilder argsBuilder = compileSubCommandArgs(subCommand.args());
                    String permission = subCommand.permission();

                    if (permission == null || sender.hasPermission(permission)) {
                        sender.sendMessage(DETools.rc(DETools.rt(backend.getAPI().getConfigManager().getMessages().getString("help-addons", "format", "command"),
                                LocalPlaceholder.of("%cmd%", commandName),
                                LocalPlaceholder.of("%args%", argsBuilder),
                                LocalPlaceholder.of("%description%", description)
                        )));
                    }
                }));
            }
        });
    }

    private static @NotNull StringBuilder compileSubCommandArgs(String[] args) {
        StringBuilder builder = new StringBuilder();
        if (args != null) {
            for (String arg : args) {
                builder.append(arg).append(" ");
            }
            builder.setLength(builder.length() - 1);
        }
        return builder;
    }

    @Override
    public List<String> getTabCompletions(@NotNull DECommandSender sender, @NotNull String label, @NotNull String[] args) {
        List<String> value = new ArrayList<>();

        if (args.length == 1) {
            Map<String, SubCommand> subCommands = backend.getAPI().getSubCommandManager().getMap();

            for (Map.Entry<String, SubCommand> entry : subCommands.entrySet()) {
                String subCommandName = entry.getKey();
                SubCommand subCommand = entry.getValue();
                String permission = subCommand.permission();

                if (permission == null || sender.hasPermission(permission)) {
                    value.add(subCommandName);
                }
            }
        } else {
            SubCommand subCommand = backend.getAPI().getSubCommandManager().get(args[0].toLowerCase());
            if(subCommand == null) return new ArrayList<>();

            return subCommand.getTabCompletions(sender, label, Arrays.copyOfRange(args, 1, args.length));
        }

        if (args[args.length - 1].isEmpty()) {
            Collections.sort(value);
            return value;
        }

        return value.stream()
                .filter(tmp -> tmp.startsWith(args[args.length - 1]))
                .sorted()
                .collect(Collectors.toList());
    }
}
