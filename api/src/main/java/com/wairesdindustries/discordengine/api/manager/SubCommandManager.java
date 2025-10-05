package com.wairesdindustries.discordengine.api.manager;

import com.wairesdindustries.discordengine.api.data.subcommand.SubCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public interface SubCommandManager {

	void register(@NotNull SubCommand subCommand);

	@Nullable SubCommand get(@NotNull String name);

	Collection<SubCommand> values();

	Map<String, SubCommand> asMap();

	boolean isRegistered(@NotNull String name);

	void unregister(@NotNull String name);

	void unregisterAll();


    @NotNull
    Map<String, SubCommand> getMap();
}


