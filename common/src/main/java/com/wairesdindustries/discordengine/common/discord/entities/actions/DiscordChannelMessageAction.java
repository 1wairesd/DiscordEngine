package com.wairesdindustries.discordengine.common.discord.entities.actions;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class DiscordChannelMessageAction implements DiscordAction {

    private final String channelId;
    private final String content;

    public DiscordChannelMessageAction(String channelId, String content) {
        this.channelId = channelId;
        this.content = content;
    }

    @Override
    public void execute(DiscordCommandContext context) {
        if (!(context instanceof com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl impl)) {
            return;
        }

        if (impl.getGuildTyped() == null) {
            context.replyEphemeral("This command can only be used in a server!");
            return;
        }

        MessageChannel channel = impl.getGuildTyped().getTextChannelById(channelId);
        if (channel == null) {
            context.replyEphemeral("Channel not found!");
            return;
        }

        channel.sendMessage(content).queue(
                success -> context.replyEphemeral("Message sent to " + channel.getName()),
                error -> context.replyEphemeral("Failed to send message: " + error.getMessage())
        );
    }
}
