package com.wairesdindustries.discordengine.api.discord.actions;

import com.wairesdindustries.discordengine.api.discord.commands.CommandContext;

public interface Action {
    void execute(CommandContext context);
}