package com.wairesdindustries.discordengine.common.discord.entities.actions;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class DiscordLockChannelAction implements DiscordAction {

    private final boolean lock;

    public DiscordLockChannelAction(boolean lock) {
        this.lock = lock;
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

        if (impl.getChannelTyped() instanceof TextChannel textChannel) {

            PermissionOverride publicRole = textChannel.getPermissionOverride(impl.getGuildTyped().getPublicRole());

            if (publicRole == null) {
                textChannel.upsertPermissionOverride(impl.getGuildTyped().getPublicRole())
                        .setDenied(lock ? Permission.MESSAGE_SEND : null)
                        .setAllowed(lock ? null : Permission.MESSAGE_SEND)
                        .queue(
                                success -> context.reply("Channel " + (lock ? "locked" : "unlocked")),
                                error -> context.replyEphemeral("Failed to " + (lock ? "lock" : "unlock") + " channel: " + error.getMessage())
                        );
            } else {
                publicRole.getManager()
                        .setDenied(lock ? Permission.MESSAGE_SEND : null)
                        .setAllowed(lock ? null : Permission.MESSAGE_SEND)
                        .queue(
                                success -> context.reply("Channel " + (lock ? "locked" : "unlocked")),
                                error -> context.replyEphemeral("Failed to " + (lock ? "lock" : "unlock") + " channel: " + error.getMessage())
                        );
            }
        } else {
            context.replyEphemeral("This command can only be used in text channels!");
        }
    }
}
