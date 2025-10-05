package com.wairesdindustries.discordengine.paper;

import com.wairesdindustries.discordengine.api.tools.DETools;
import com.wairesdindustries.discordengine.common.DiscordEngine;
import com.wairesdindustries.discordengine.common.platform.BackendPlatform;
import com.wairesdindustries.discordengine.paper.tools.ToolsImpl;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Logger;

public class DEPaperBackend extends BackendPlatform {
    private final DETools tools;
	private final DEPaperBootstrap plugin;
	private final DiscordEngine api;

	public DEPaperBackend(DEPaperBootstrap plugin) {
		this.plugin = plugin;
        this.api = new DiscordEngine(this);
        this.tools = new ToolsImpl(this);
	}

    public void load() {
        api.load();

        registerDefaultCommand();
    }

    public void unload() {
		api.unload();
    }

	@Override
	public DiscordEngine getAPI() {
		return api;
	}

    @Override
    public String getName() {
        return plugin.getName();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String getIdentifier() {
        return "Bukkit";
    }

    @Override
    public DETools getTools() {
        return tools;
    }

    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }

    @Override
    public @NotNull File getDataFolder() {
        return plugin.getDataFolder();
    }

    private void registerDefaultCommand() {
        PluginCommand command = plugin.getCommand("discordengine");
        DEPaperCommand dePaperCommand = new DEPaperCommand(this);
        if (command != null) {
            command.setExecutor(dePaperCommand);
            command.setTabCompleter(dePaperCommand);
        }
    }
}
