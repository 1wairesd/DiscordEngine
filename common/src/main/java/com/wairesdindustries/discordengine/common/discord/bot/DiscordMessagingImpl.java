package com.wairesdindustries.discordengine.common.discord.bot;

import com.wairesdindustries.discordengine.api.discord.bot.DiscordMessaging;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.concurrent.CompletableFuture;

public class DiscordMessagingImpl implements DiscordMessaging {

    private final DiscordBotServiceImpl botService;

    public DiscordMessagingImpl(DiscordBotServiceImpl botService) {
        this.botService = botService;
    }

    private JDA getJda() {
        return botService.getJda();
    }

    @Override
    public CompletableFuture<Void> sendMessageToChannel(String channelId, String message) {
        TextChannel channel = getJda().getTextChannelById(channelId);
        if (channel == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Channel not found: " + channelId));
        }
        CompletableFuture<Void> cf = new CompletableFuture<>();
        channel.sendMessage(message).queue(s -> cf.complete(null), cf::completeExceptionally);
        return cf;
    }

    @Override
    public CompletableFuture<Void> deleteCommand(String trigger) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        getJda().retrieveCommands().queue(commands -> {
            commands.stream()
                    .filter(c -> c.getName().equalsIgnoreCase(trigger))
                    .forEach(c -> getJda().deleteCommandById(c.getId()).queue());
            cf.complete(null);
        });
        return cf;
    }

    @Override
    public CompletableFuture<Void> deleteAllCommands() {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        getJda().updateCommands().queue(s -> cf.complete(null), cf::completeExceptionally);
        return cf;
    }
}