package com.wairesdindustries.discordengine.api.discord.commands;

import com.wairesdindustries.discordengine.api.discord.actions.Action;

import java.util.List;

public interface Command {
    String getName();
    String getDescription();
    List<Action> getActions();
    void execute(CommandContext context);
}
