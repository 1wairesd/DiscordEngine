package com.wairesdindustries.discordengine.common.discord.command;

import com.wairesdindustries.discordengine.api.discord.command.DiscordCommand;
import com.wairesdindustries.discordengine.api.discord.command.DiscordCommandManager;
import com.wairesdindustries.discordengine.common.DiscordEngine;
import com.wairesdindustries.discordengine.common.discord.bot.DiscordBotServiceImpl;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class DiscordCommandManagerImpl extends ListenerAdapter implements DiscordCommandManager {

    private final DiscordEngine api;
    private final Map<String, DiscordCommand> slashCommands = new HashMap<>();
    private boolean listenersRegistered = false;

    public DiscordCommandManagerImpl(DiscordEngine api) {
        this.api = api;
    }

    private JDA getJDA() {
        if (api.getDiscordBotService() instanceof DiscordBotServiceImpl impl) {
            return impl.getJDA();
        }
        return null;
    }

    @Override
    public void registerCommand(DiscordCommand command) {
        if (command instanceof DiscordCommandImpl c) {
            slashCommands.put(c.getTrigger(), c);
        }
    }

    @Override
    public void registerAll() {
        JDA jda = getJDA();
        if (jda == null) return;

        for (DiscordCommand cmd : slashCommands.values()) {
            if (cmd instanceof DiscordCommandImpl c) {
                jda.upsertCommand(c.getTrigger(), c.getDescription()).queue();
            }
        }

        if (!listenersRegistered) {
            jda.addEventListener(this);
            listenersRegistered = true;
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        DiscordCommand command = slashCommands.get(event.getName());
        if (command != null) command.execute(new DiscordCommandContextImpl(event));
    }
}
