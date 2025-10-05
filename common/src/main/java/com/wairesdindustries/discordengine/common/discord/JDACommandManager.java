package com.wairesdindustries.discordengine.common.discord;

import com.wairesdindustries.discordengine.api.discord.commands.Command;
import com.wairesdindustries.discordengine.api.discord.commands.CommandManager;
import com.wairesdindustries.discordengine.common.DiscordEngine;
import com.wairesdindustries.discordengine.common.discord.commands.JDACommandContext;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class JDACommandManager extends ListenerAdapter implements CommandManager {

    private final DiscordEngine api;
    private final Map<String, Command> commands = new HashMap<>();
    private boolean listenersRegistered = false;

    public JDACommandManager(DiscordEngine api) {
        this.api = api;
    }

    private JDA getJDA() {
        return api.getBotService().getJDA();
    }

    @Override
    public void registerCommand(Command command) {
        commands.put(command.getName(), command);
    }

    @Override
    public void registerAll() {
        JDA jda = getJDA();
        for (Command cmd : commands.values()) {
            jda.upsertCommand(cmd.getName(), cmd.getDescription()).queue();
        }
        if (!listenersRegistered) {
            jda.addEventListener(this);
            listenersRegistered = true;
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Command command = commands.get(event.getName());
        if (command != null) {
            command.execute(new JDACommandContext(event));
        }
    }

}
