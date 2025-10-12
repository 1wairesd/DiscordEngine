package com.wairesdindustries.discordengine.common.discord.command;

import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommand;
import com.wairesdindustries.discordengine.api.discord.command.DiscordCommandManager;
import com.wairesdindustries.discordengine.common.DiscordEngine;
import com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl;
import com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandImpl;
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
        if (command instanceof DiscordCommandImpl c) {
            commands.put(c.getTrigger(), c);
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        JDA jda = event.getJDA();
        for (DiscordCommand cmd : commands.values()) {
            if (cmd instanceof DiscordCommandImpl c) {
                jda.upsertCommand(c.getTrigger(), c.getDescription()).queue();
            }
        }
    }

    @Override
    public void registerAll() {
        // This is now handled by the onReady event
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        DiscordCommand command = commands.get(event.getName());
        if (command != null) {
            command.execute(new DiscordCommandContextImpl(event));
        }
    }
}
