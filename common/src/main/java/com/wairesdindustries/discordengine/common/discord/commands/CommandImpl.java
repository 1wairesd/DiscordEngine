package com.wairesdindustries.discordengine.common.discord.commands;

import com.wairesdindustries.discordengine.api.discord.commands.CommandContext;
import com.wairesdindustries.discordengine.api.discord.actions.Action;
import com.wairesdindustries.discordengine.api.discord.commands.Command;

import java.util.List;

public class CommandImpl implements Command {

    private final String name;
    private final String description;
    private final List<Action> actions;

    public CommandImpl(String name, String description, List<Action> actions) {
        this.name = name;
        this.description = description;
        this.actions = actions;
    }

    @Override
    public String getName() { return name; }

    @Override
    public String getDescription() { return description; }

    @Override
    public List<Action> getActions() { return actions; }

    @Override
    public void execute(CommandContext context) {
        for (Action action : actions) {
            action.execute(context);
        }
    }
}
