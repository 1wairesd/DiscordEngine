package com.wairesdindustries.discordengine.common.discord.entities.actions;

import java.util.List;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class DiscordDeleteMessagesAction implements DiscordAction {

    private final int amount;

    public DiscordDeleteMessagesAction(int amount) {
        this.amount = Math.min(Math.max(amount, 1), 100);
    }

    @Override
    public void execute(DiscordCommandContext context) {
        if (!(context instanceof com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl impl)) {
            return;
        }

        if (!context.hasPermission(Permission.MESSAGE_MANAGE.name())) {
            context.replyEphemeral("You don't have permission to manage messages!");
            return;
        }

        MessageChannel channel = impl.getChannelTyped();
        if (channel == null) {
            context.replyEphemeral("Could not access channel!");
            return;
        }

        channel.getIterableHistory()
                .takeAsync(amount)
                .thenAccept(messages -> {
                    List<Message> toDelete = messages.stream()
                            .filter(msg -> !msg.getTimeCreated().isBefore(
                                    java.time.OffsetDateTime.now().minusWeeks(2)))
                            .toList();

                    if (toDelete.isEmpty()) {
                        context.replyEphemeral("No messages to delete!");
                        return;
                    }

                    if (toDelete.size() == 1) {
                        toDelete.get(0).delete().queue();
                    } else if (channel instanceof net.dv8tion.jda.api.entities.channel.concrete.TextChannel textChannel) {
                        textChannel.deleteMessages(toDelete).queue();
                    }

                    context.replyEphemeral("Deleted " + toDelete.size() + " messages!");
                })
                .exceptionally(error -> {
                    context.replyEphemeral("Failed to delete messages: " + error.getMessage());
                    return null;
                });
    }
}
