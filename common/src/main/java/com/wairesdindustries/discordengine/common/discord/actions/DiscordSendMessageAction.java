package com.wairesdindustries.discordengine.common.discord.actions;

import com.wairesdindustries.discordengine.api.discord.command.DiscordCommandContext;
import com.wairesdindustries.discordengine.api.discord.actions.DiscordAction;

public class DiscordSendMessageAction implements DiscordAction {

    private final String content;

    public DiscordSendMessageAction(String content) {
        this.content = content;
    }

    @Override
    public void execute(DiscordCommandContext context) {
        context.reply(content);
    }
}