package com.wairesdindustries.discordengine.common.discord.entities.actions;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;

public class DiscordPrivateMessageAction implements DiscordAction {

    private final String content;

    public DiscordPrivateMessageAction(String content) {
        this.content = content;
    }

    @Override
    public void execute(DiscordCommandContext context) {
        if (content != null && !content.isEmpty()) {
            context.sendPrivateMessage(content);
        }
    }
}
