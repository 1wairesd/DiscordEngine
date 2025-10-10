package com.wairesdindustries.discordengine.common.command.sub;

import com.wairesdindustries.discordengine.api.DEAPI;
import com.wairesdindustries.discordengine.api.data.subcommand.SubCommandType;
import com.wairesdindustries.discordengine.api.platform.DECommandSender;
import com.wairesdindustries.discordengine.common.command.DefaultCommand;
import org.jetbrains.annotations.NotNull;

public class HelpCommand extends DefaultCommand {

    private final DEAPI api;

    public HelpCommand(DEAPI api) {
        super(api,"help", SubCommandType.PLAYER);
        this.api = api;
    }

    @Override
	public boolean execute(@NotNull DECommandSender sender, @NotNull String label, @NotNull String[] args) {
		return false;
	}

}


