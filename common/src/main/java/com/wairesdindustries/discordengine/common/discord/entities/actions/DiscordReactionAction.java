package com.wairesdindustries.discordengine.common.discord.entities.actions;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;

public class DiscordReactionAction implements DiscordAction {

    private final String emoji;

    public DiscordReactionAction(String emoji) {
        this.emoji = emoji;
    }

    @Override
    public void execute(DiscordCommandContext context) {
        if (emoji != null && !emoji.isEmpty()) {
            context.addReaction(emoji);
        }
    }
}
