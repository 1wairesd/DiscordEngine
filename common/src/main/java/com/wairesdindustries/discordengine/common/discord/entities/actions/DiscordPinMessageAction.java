package com.wairesdindustries.discordengine.common.discord.entities.actions;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;
import com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl;

import net.dv8tion.jda.api.Permission;

public class DiscordPinMessageAction implements DiscordAction {

    private final boolean pin;

    public DiscordPinMessageAction(boolean pin) {
        this.pin = pin;
    }

    @Override
    public void execute(DiscordCommandContext context) {
        if (!(context instanceof DiscordCommandContextImpl impl)) {
            return;
        }

        if (!context.hasPermission(Permission.MESSAGE_MANAGE.name())) {
            context.replyEphemeral("You don't have permission to manage messages!");
            return;
        }

        impl.getSlashEvent().getHook().retrieveOriginal().queue(message -> {
            if (pin) {
                message.pin().queue(
                        success -> context.reply("Message pinned!"),
                        error -> context.replyEphemeral("Failed to pin message: " + error.getMessage())
                );
            } else {
                message.unpin().queue(
                        success -> context.reply("Message unpinned!"),
                        error -> context.replyEphemeral("Failed to unpin message: " + error.getMessage())
                );
            }
        });
    }
}
