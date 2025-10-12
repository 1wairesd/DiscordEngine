package com.wairesdindustries.discordengine.common.command.sub;

import com.wairesdindustries.discordengine.api.DEAPI;
import com.wairesdindustries.discordengine.api.data.subcommand.SubCommandType;
import com.wairesdindustries.discordengine.api.platform.DECommandSender;
import com.wairesdindustries.discordengine.common.command.DefaultCommand;
import com.wairesdindustries.discordengine.api.tools.DETools;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReloadCommand extends DefaultCommand {

    private final DEAPI api;

	public ReloadCommand(DEAPI api) {
		super(api, "reload", SubCommandType.PLAYER);
        this.api = api;
	}

    @Override
    public List<String> getTabCompletions(@NotNull DECommandSender sender, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }

    @Override
    public boolean execute(@NotNull DECommandSender sender, @NotNull String label, @NotNull String[] args) {
        DEAPI api = DEAPI.getInstance();
        load();
        sender.sendMessage(DETools.prefix(
                api.getConfigManager().getMessages().getString("config-reloaded", "§aConfiguration reloaded!")
        ));
        return true;
    }

    private void load() {
        api.getConfigManager().load();

        refreshDiscordCommands();
        refreshBotStatus();
    }

    private void refreshDiscordCommands() {
        api.getDiscordCommandLoader().load();
        api.getDiscordCommandManager().registerAll();
    }

    private void refreshBotStatus() {
        api.getDiscordAvatarService().updateAvatar();
        api.getDiscordAvatarService().updateActivity("Discord Engine");
    }

}


