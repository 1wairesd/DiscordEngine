package com.wairesdindustries.discordengine.common.command.sub;

import com.wairesdindustries.discordengine.api.DEAPI;
import com.wairesdindustries.discordengine.api.data.subcommand.SubCommandType;
import com.wairesdindustries.discordengine.api.platform.DECommandSender;
import com.wairesdindustries.discordengine.common.command.DefaultCommand;
import org.jetbrains.annotations.NotNull;

public class HelpSubCommand extends DefaultCommand {

    public HelpSubCommand(DEAPI api) {
        super(api,"help", SubCommandType.PLAYER);
    }

    @Override
	public boolean execute(@NotNull DECommandSender sender, @NotNull String label, @NotNull String[] args) {
		return false;
	}

}


