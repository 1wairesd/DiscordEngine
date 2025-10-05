package com.wairesdindustries.discordengine.common.discord.actions;

import com.wairesdindustries.discordengine.api.discord.commands.CommandContext;
import com.wairesdindustries.discordengine.api.discord.actions.Action;

public class SendMessageAction implements Action {

    private final String content;

    public SendMessageAction(String content) {
        this.content = content;
    }

    @Override
    public void execute(CommandContext context) {
        context.reply(content);
    }
}