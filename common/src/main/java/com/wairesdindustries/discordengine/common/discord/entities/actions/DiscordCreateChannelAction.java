package com.wairesdindustries.discordengine.common.discord.entities.actions;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;

import net.dv8tion.jda.api.Permission;

public class DiscordCreateChannelAction implements DiscordAction {

    private final String channelName;
    private final String channelType;

    public DiscordCreateChannelAction(String channelName, String channelType) {
        this.channelName = channelName;
        this.channelType = channelType != null ? channelType : "TEXT";
    }

    @Override
    public void execute(DiscordCommandContext context) {
        if (!(context instanceof com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl impl)) {
            return;
        }

        if (!context.hasPermission(Permission.MANAGE_CHANNEL.name())) {
            context.replyEphemeral("You don't have permission to manage channels!");
            return;
        }

        if (impl.getGuildTyped() == null) {
            context.replyEphemeral("This command can only be used in a server!");
            return;
        }

        try {
            if ("VOICE".equalsIgnoreCase(channelType)) {
                impl.getGuildTyped().createVoiceChannel(channelName).queue(
                        channel -> context.reply("Voice channel created: " + channel.getName()),
                        error -> context.replyEphemeral("Failed to create channel: " + error.getMessage())
                );
            } else {
                impl.getGuildTyped().createTextChannel(channelName).queue(
                        channel -> context.reply("Text channel created: " + channel.getName()),
                        error -> context.replyEphemeral("Failed to create channel: " + error.getMessage())
                );
            }
        } catch (Exception e) {
            context.replyEphemeral("Failed to create channel: " + e.getMessage());
        }
    }
}
