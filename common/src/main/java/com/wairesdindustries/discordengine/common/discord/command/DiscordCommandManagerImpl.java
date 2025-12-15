package com.wairesdindustries.discordengine.common.discord.command;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.wairesdindustries.discordengine.api.discord.command.DiscordCommandManager;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommand;
import com.wairesdindustries.discordengine.common.DiscordEngine;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordCommandManagerImpl extends ListenerAdapter implements DiscordCommandManager {

    private final DiscordEngine api;
    private final Map<String, String> commandDescriptions = new HashMap<>();

    public DiscordCommandManagerImpl(DiscordEngine api) {
        this.api = api;
    }

    @Override
    public void registerCommand(DiscordCommand command) {
        commandDescriptions.put(command.getTrigger(), command.getDescription());
    }

    @Override
    public void registerCommand(String name, String description) {
        commandDescriptions.put(name, description);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        JDA jda = event.getJDA();

        Map<String, String> commands = api.getFlowManager().getEntryPointRegistry().getAllCommands();
        for (String commandName : commands.keySet()) {
            String description = commandDescriptions.getOrDefault(commandName, "Flow-based command");
            jda.upsertCommand(commandName, description).queue();
        }
    }

    @Override
    public void registerAll() {

    }


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

    }
}
