package com.wairesdindustries.discordengine.common.discord.entities.actions;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class DiscordSlowmodeAction implements DiscordAction {

    private final int seconds;

    public DiscordSlowmodeAction(int seconds) {
        this.seconds = Math.min(Math.max(seconds, 0), 21600);
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

        if (impl.getChannelTyped() instanceof TextChannel textChannel) {
            textChannel.getManager().setSlowmode(seconds).queue(
                    success -> context.reply("Slowmode set to " + seconds + " seconds"),
                    error -> context.replyEphemeral("Failed to set slowmode: " + error.getMessage())
            );
        } else {
            context.replyEphemeral("This command can only be used in text channels!");
        }
    }
}
