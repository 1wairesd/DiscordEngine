package com.wairesdindustries.discordengine.common.discord.entities.command;

import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommand;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;
import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;

import java.util.List;

public class DiscordCommandImpl implements DiscordCommand {

    private final String name;
    private final String trigger;
    private final String description;
    private final List<DiscordAction> actions;

    public DiscordCommandImpl(String name, String trigger, String description, List<DiscordAction> actions) {
        this.name = name;
        this.trigger = trigger;
        this.description = description;
        this.actions = actions;
    }

    @Override
    public String getName() { return name; }

    @Override
    public String getTrigger() { return trigger; }

    @Override
    public String getDescription() { return description; }

    @Override
    public List<DiscordAction> getActions() { return actions; }

    @Override
    public void execute(DiscordCommandContext context) {
        for (DiscordAction action : actions) {
            action.execute(context);
        }
    }
}
