package com.wairesdindustries.discordengine.common.manager;

import com.wairesdindustries.discordengine.api.DEAPI;
import com.wairesdindustries.discordengine.api.data.subcommand.SubCommand;
import com.wairesdindustries.discordengine.api.manager.SubCommandManager;
import com.wairesdindustries.discordengine.api.platform.Platform;
import com.wairesdindustries.discordengine.common.command.DefaultCommand;
import com.wairesdindustries.discordengine.common.command.sub.HelpCommand;
import com.wairesdindustries.discordengine.common.command.sub.ReloadCommand;
import com.wairesdindustries.discordengine.common.command.sub.DiscordCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class SubCommandManagerImpl implements SubCommandManager {

    private final DEAPI api;
    private final Platform platform;

    public SubCommandManagerImpl(DEAPI api) {
        this.api = api;
        this.platform = api.getPlatform();

        registerDefault();
    }

    private static final List<? extends Class<? extends DefaultCommand>> defaultCommands = Arrays.asList(
            HelpCommand.class,
            ReloadCommand.class,
            DiscordCommand.class
    );


    private final Map<String, SubCommand> registeredSubCommands = new ConcurrentHashMap<>();

	@Override
	public void register(@NotNull SubCommand subCommand) {
        registeredSubCommands.put(subCommand.name().toLowerCase(Locale.ROOT), subCommand);
	}

	@Override
	public @Nullable SubCommand get(@NotNull String name) {
		return registeredSubCommands.get(name.toLowerCase(Locale.ROOT));
	}

	@Override
	public Collection<SubCommand> values() {
		return registeredSubCommands.values();
	}

	@Override
	public Map<String, SubCommand> asMap() {
		return Collections.unmodifiableMap(registeredSubCommands);
	}

	@Override
	public boolean isRegistered(@NotNull String name) {
		return registeredSubCommands.containsKey(name.toLowerCase(Locale.ROOT));
	}

	@Override
	public void unregister(@NotNull String name) {
        registeredSubCommands.remove(name.toLowerCase(Locale.ROOT));
	}

	@Override
	public void unregisterAll() {
        registeredSubCommands.clear();
	}

    @Override
    public @NotNull Map<String, SubCommand> getMap() {
        return registeredSubCommands;
    }

    private void registerDefault() {
        defaultCommands.forEach(commandClass -> {
            try {
                DefaultCommand command = commandClass.getDeclaredConstructor(DEAPI.class).newInstance(api);
                register(command.build());
            } catch (Exception e) {
                platform.getLogger().log(Level.WARNING, "Failed to register command: " + commandClass.getSimpleName(), e);
            }
        });
    }
}


