package com.wairesdindustries.discordengine.common.discord.entities.actions;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;

public class DiscordEphemeralMessageAction implements DiscordAction {

    private final String content;

    public DiscordEphemeralMessageAction(String content) {
        this.content = content;
    }

    @Override
    public void execute(DiscordCommandContext context) {
        context.replyEphemeral(content);
    }
}
