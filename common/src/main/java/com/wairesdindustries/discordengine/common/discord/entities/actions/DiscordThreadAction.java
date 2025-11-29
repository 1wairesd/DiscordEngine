package com.wairesdindustries.discordengine.common.discord.entities.actions;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;
import com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl;

import net.dv8tion.jda.api.Permission;

public class DiscordThreadAction implements DiscordAction {

    private final String threadName;
    private final boolean privateThread;

    public DiscordThreadAction(String threadName, boolean privateThread) {
        this.threadName = threadName;
        this.privateThread = privateThread;
    }

    @Override
    public void execute(DiscordCommandContext context) {
        if (!(context instanceof DiscordCommandContextImpl impl)) {
            return;
        }

        if (!context.hasPermission(Permission.CREATE_PUBLIC_THREADS.name()) && !privateThread) {
            context.replyEphemeral("You don't have permission to create public threads!");
            return;
        }

        if (!context.hasPermission(Permission.CREATE_PRIVATE_THREADS.name()) && privateThread) {
            context.replyEphemeral("You don't have permission to create private threads!");
            return;
        }

        impl.getSlashEvent().getHook().retrieveOriginal().queue(message -> {
            if (privateThread) {
                message.createThreadChannel(threadName).queue(
                        thread -> context.reply("Private thread created: " + thread.getName()),
                        error -> context.replyEphemeral("Failed to create thread: " + error.getMessage())
                );
            } else {
                message.createThreadChannel(threadName).queue(
                        thread -> context.reply("Thread created: " + thread.getName()),
                        error -> context.replyEphemeral("Failed to create thread: " + error.getMessage())
                );
            }
        });
    }
}
