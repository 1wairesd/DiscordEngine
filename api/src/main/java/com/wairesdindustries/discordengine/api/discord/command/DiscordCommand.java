package com.wairesdindustries.discordengine.api.discord.command;

import com.wairesdindustries.discordengine.api.discord.actions.DiscordAction;

import java.util.List;

public interface DiscordCommand {

    String getName();

    String getTrigger();

    String getDescription();

    List<DiscordAction> getActions();

    void execute(DiscordCommandContext context);

}