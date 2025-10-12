package com.wairesdindustries.discordengine.common.command.sub;

import com.wairesdindustries.discordengine.api.DEAPI;
import com.wairesdindustries.discordengine.api.data.subcommand.SubCommandType;
import com.wairesdindustries.discordengine.api.platform.DECommandSender;
import com.wairesdindustries.discordengine.api.tools.DETools;
import com.wairesdindustries.discordengine.common.command.DefaultCommand;
import com.wairesdindustries.discordengine.common.tools.LocalPlaceholder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class DiscordCommand extends DefaultCommand {

    private final DEAPI api;

    public DiscordCommand(DEAPI api) {
        super(api, "discord", SubCommandType.ADMIN);
        this.api = api;
    }

    @Override
    public boolean execute(@NotNull DECommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) return false;
        if (!"command".equalsIgnoreCase(args[0])) return false;
        if (args.length < 2) return false;
        if (!"delete".equalsIgnoreCase(args[1])) return false;

        if (args.length >= 3) {
            String trigger = args[2];
            api.getDiscordMessagingService().deleteCommand(trigger);

            String template = api.getConfigManager().getMessages()
                    .getString("discord-command-deleted", "&aCommand %trigger% unlinked in Discord!");
            String msg = DETools.rt(template, new LocalPlaceholder("%trigger%", trigger));
            sender.sendMessage(DETools.prefix(msg));
            return true;
        }

        String warning = api.getConfigManager().getMessages()
                .getString("discord-command-delete-confirm", "&cYou are about to unlink all commands registered in Discord! Please confirm by entering Y or N.");
        sender.sendMessage(DETools.prefix(DETools.rc(warning)));

        String invalidInputMsg = api.getConfigManager().getMessages()
                .getString("confirmation-invalid-input", "§cInvalid input. Please enter &aY &cor &cN.");
        api.getConfirmationManager().requestConfirmation(
                sender,
                Map.of(
                        "y", s -> {
                            api.getDiscordMessagingService().deleteAllCommands();
                            String deletedAllMsg = api.getConfigManager().getMessages()
                                    .getString("discord-command-delete-all", "§aAll commands in Discord have been unlinked!");
                            s.sendMessage(DETools.prefix(DETools.rc(deletedAllMsg)));
                        },
                        "n", s -> {
                            String canceledMsg = api.getConfigManager().getMessages()
                                    .getString("discord-command-delete-cancel", "§cCommand unlinking cancelled.");
                            s.sendMessage(DETools.prefix(DETools.rc(canceledMsg)));
                        }
                ),
                DETools.prefix(DETools.rc(invalidInputMsg)),
                30
        );

        return true;
    }

    @Override
    public List<String> getTabCompletions(@NotNull DECommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) return List.of("command");
        if (args.length == 2) return List.of("delete");
        return List.of();
    }

}
