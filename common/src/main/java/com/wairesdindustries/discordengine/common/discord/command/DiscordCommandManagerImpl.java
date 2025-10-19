package com.wairesdindustries.discordengine.common.discord.command;

import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommand;
import com.wairesdindustries.discordengine.api.discord.command.DiscordCommandManager;
import com.wairesdindustries.discordengine.common.DiscordEngine;
import com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class DiscordCommandManagerImpl extends ListenerAdapter implements DiscordCommandManager {

    private final DiscordEngine api;
    private final Map<String, DiscordCommand> commands = new HashMap<>();

    public DiscordCommandManagerImpl(DiscordEngine api) {
        this.api = api;
    }

    @Override
    public void registerCommand(DiscordCommand command) {
        commands.put(command.getTrigger(), command);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        JDA jda = event.getJDA();
        for (DiscordCommand cmd : commands.values()) {
            jda.upsertCommand(cmd.getTrigger(), cmd.getDescription()).queue();
        }
    }

    @Override
    public void registerAll() {
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        DiscordCommand command = commands.get(event.getName());
        if (command != null) {
            java.util.concurrent.CompletableFuture.runAsync(() -> {
                try {
                    command.execute(new DiscordCommandContextImpl(event));
                } catch (Exception e) {
                    event.reply("An error occurred while executing the command").setEphemeral(true).queue();
                }
            });
        }
    }
}
