package com.wairesdindustries.discordengine.common.command;

import com.wairesdindustries.discordengine.api.DEAPI;
import com.wairesdindustries.discordengine.api.data.subcommand.SubCommand;
import com.wairesdindustries.discordengine.api.data.subcommand.SubCommandExecutor;
import com.wairesdindustries.discordengine.api.data.subcommand.SubCommandTabCompleter;
import com.wairesdindustries.discordengine.api.data.subcommand.SubCommandType;
import com.wairesdindustries.discordengine.api.platform.DECommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class DefaultCommand implements SubCommandExecutor, SubCommandTabCompleter {

    private final DEAPI api;
    private final SubCommandType type;
    private final String name;


    public DefaultCommand(DEAPI api, String name, SubCommandType type) {
        this.api = api;
        this.type = type;
        this.name = name;
    }

    public final SubCommand build() {
        return SubCommand.builder()
                .addon(api.getPlatform())
                .name(name)
                .permission(type.permission)
                .tabCompleter(this)
                .executor(this)
                .build();
    }

	@Override
    public List<String> getTabCompletions(@NotNull DECommandSender sender, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}


