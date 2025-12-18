package com.wairesdindustries.discordengine.common.command.sub;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.wairesdindustries.discordengine.api.DEAPI;
import com.wairesdindustries.discordengine.api.data.subcommand.SubCommandType;
import com.wairesdindustries.discordengine.api.platform.DECommandSender;
import com.wairesdindustries.discordengine.api.tools.DETools;
import com.wairesdindustries.discordengine.common.command.DefaultCommand;

public class ReloadCommand extends DefaultCommand {

    private final DEAPI api;

	public ReloadCommand(DEAPI api) {
		super(api, "reload", SubCommandType.PLAYER);
        this.api = api;
	}

    @Override
    public List<String> getTabCompletions(@NotNull DECommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("bot");
        } else if (args.length == 2 && "bot".equals(args[0])) {
            return List.of("default");
        } else if (args.length == 3 && "bot".equals(args[0])) {
            return List.of("restart", "reconnect", "config");
        }
        return List.of();
    }

    @Override
    public boolean execute(@NotNull DECommandSender sender, @NotNull String label, @NotNull String[] args) {
        DEAPI api = DEAPI.getInstance();
        
        if (args.length == 0) {
            load();
            sender.sendMessage(DETools.prefix(
                    api.getConfigManager().getMessages().getString("config-reloaded", "§aConfiguration reloaded!")
            ));
        } else if (args.length >= 2 && "bot".equals(args[0])) {
            String botName = args[1];
            String reloadType = args.length > 2 ? args[2] : "restart";
            
            api.getDiscordBotManager().reloadBot(botName, reloadType);
            sender.sendMessage(DETools.prefix("§aBot " + botName + " reloaded (" + reloadType + ")"));
        } else {
            sender.sendMessage(DETools.prefix("§cUsage: /de reload [bot <name> [type]]"));
        }
        
        return true;
    }

    private void load() {
        api.getConfigManager().load();
        api.getFlowManager().load();

        refreshDiscordCommands();
        refreshBotStatus();
    }

    private void refreshDiscordCommands() {
        api.getDiscordCommandManager().registerAll();
    }

    private void refreshBotStatus() {
        api.getDiscordAvatarService().updateAvatar();
        api.getDiscordAvatarService().updateActivity("Discord Engine");
    }

}


