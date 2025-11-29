package com.wairesdindustries.discordengine.common.discord.entities.command;

import java.util.List;

import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class DiscordCommandContextImpl implements DiscordCommandContext {

    private final SlashCommandInteractionEvent slashEvent;

    public DiscordCommandContextImpl(SlashCommandInteractionEvent event) {
        this.slashEvent = event;
    }

    @Override
    public void reply(String message) {
        if (this.slashEvent != null) {
            this.slashEvent.reply(message).queue();
        }
    }

    @Override
    public void replyEphemeral(String message) {
        if (this.slashEvent != null) {
            this.slashEvent.reply(message).setEphemeral(true).queue();
        }
    }

    @Override
    public void replyEmbed(Object embed) {
        if (this.slashEvent != null && embed instanceof MessageEmbed) {
            this.slashEvent.replyEmbeds((MessageEmbed) embed).queue();
        }
    }

    @Override
    public void deferReply() {
        if (this.slashEvent != null) {
            this.slashEvent.deferReply().queue();
        }
    }

    @Override
    public void deferReplyEphemeral() {
        if (this.slashEvent != null) {
            this.slashEvent.deferReply(true).queue();
        }
    }

    @Override
    public Object getUser() {
        return slashEvent != null ? slashEvent.getUser() : null;
    }

    @Override
    public Object getMember() {
        return slashEvent != null ? slashEvent.getMember() : null;
    }

    @Override
    public Object getGuild() {
        return slashEvent != null ? slashEvent.getGuild() : null;
    }

    @Override
    public Object getChannel() {
        return slashEvent != null ? slashEvent.getChannel() : null;
    }
    
    public User getUserTyped() {
        return slashEvent != null ? slashEvent.getUser() : null;
    }

    public Member getMemberTyped() {
        return slashEvent != null ? slashEvent.getMember() : null;
    }

    public Guild getGuildTyped() {
        return slashEvent != null ? slashEvent.getGuild() : null;
    }

    public MessageChannel getChannelTyped() {
        return slashEvent != null ? slashEvent.getChannel() : null;
    }

    @Override
    public String getOptionAsString(String name) {
        if (slashEvent == null) return null;
        OptionMapping option = slashEvent.getOption(name);
        return option != null ? option.getAsString() : null;
    }

    @Override
    public Long getOptionAsLong(String name) {
        if (slashEvent == null) return null;
        OptionMapping option = slashEvent.getOption(name);
        return option != null ? option.getAsLong() : null;
    }

    @Override
    public Integer getOptionAsInteger(String name) {
        if (slashEvent == null) return null;
        OptionMapping option = slashEvent.getOption(name);
        return option != null ? option.getAsInt() : null;
    }

    @Override
    public Boolean getOptionAsBoolean(String name) {
        if (slashEvent == null) return null;
        OptionMapping option = slashEvent.getOption(name);
        return option != null ? option.getAsBoolean() : null;
    }

    @Override
    public Object getOptionAsUser(String name) {
        if (slashEvent == null) return null;
        OptionMapping option = slashEvent.getOption(name);
        return option != null ? option.getAsUser() : null;
    }

    @Override
    public Object getOptionAsMember(String name) {
        if (slashEvent == null) return null;
        OptionMapping option = slashEvent.getOption(name);
        return option != null ? option.getAsMember() : null;
    }

    @Override
    public Object getOptionAsRole(String name) {
        if (slashEvent == null) return null;
        OptionMapping option = slashEvent.getOption(name);
        return option != null ? option.getAsRole() : null;
    }

    @Override
    public Object getOptionAsChannel(String name) {
        if (slashEvent == null) return null;
        OptionMapping option = slashEvent.getOption(name);
        return option != null ? option.getAsChannel() : null;
    }

    @Override
    public List<?> getOptions() {
        return slashEvent != null ? slashEvent.getOptions() : List.of();
    }
    
    public User getOptionAsUserTyped(String name) {
        if (slashEvent == null) return null;
        OptionMapping option = slashEvent.getOption(name);
        return option != null ? option.getAsUser() : null;
    }

    public Member getOptionAsMemberTyped(String name) {
        if (slashEvent == null) return null;
        OptionMapping option = slashEvent.getOption(name);
        return option != null ? option.getAsMember() : null;
    }

    public Role getOptionAsRoleTyped(String name) {
        if (slashEvent == null) return null;
        OptionMapping option = slashEvent.getOption(name);
        return option != null ? option.getAsRole() : null;
    }

    public MessageChannel getOptionAsChannelTyped(String name) {
        if (slashEvent == null) return null;
        OptionMapping option = slashEvent.getOption(name);
        return option != null ? option.getAsChannel().asGuildMessageChannel() : null;
    }

    @Override
    public void addReaction(String emoji) {
        if (slashEvent != null) {
            try {
                Emoji emojiObj = emoji.startsWith("<") ? Emoji.fromFormatted(emoji) : Emoji.fromUnicode(emoji);
                slashEvent.getHook().retrieveOriginal().queue(message -> message.addReaction(emojiObj).queue());
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void sendPrivateMessage(String message) {
        if (slashEvent != null && slashEvent.getUser() != null) {
            slashEvent.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        if (slashEvent == null || slashEvent.getMember() == null) return false;
        try {
            Permission perm = Permission.valueOf(permission);
            return slashEvent.getMember().hasPermission(perm);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public SlashCommandInteractionEvent getSlashEvent() {
        return slashEvent;
    }
}
