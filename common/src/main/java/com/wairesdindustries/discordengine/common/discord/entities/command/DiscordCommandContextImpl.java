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
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class DiscordCommandContextImpl implements DiscordCommandContext {

    private final SlashCommandInteractionEvent slashEvent;
    private final ButtonInteractionEvent buttonEvent;
    private final ModalInteractionEvent modalEvent;

    public DiscordCommandContextImpl(SlashCommandInteractionEvent event) {
        this.slashEvent = event;
        this.buttonEvent = null;
        this.modalEvent = null;
    }

    public DiscordCommandContextImpl(ButtonInteractionEvent event) {
        this.slashEvent = null;
        this.buttonEvent = event;
        this.modalEvent = null;
    }

    public DiscordCommandContextImpl(ModalInteractionEvent event) {
        this.slashEvent = null;
        this.buttonEvent = null;
        this.modalEvent = event;
    }

    @Override
    public void reply(String message) {
        if (this.slashEvent != null) {
            if (!this.slashEvent.isAcknowledged()) {
                this.slashEvent.reply(message).queue();
            } else {
                this.slashEvent.getHook().sendMessage(message).queue();
            }
        } else if (this.buttonEvent != null) {
            if (!this.buttonEvent.isAcknowledged()) {
                this.buttonEvent.reply(message).queue();
            } else {
                this.buttonEvent.getHook().sendMessage(message).queue();
            }
        } else if (this.modalEvent != null) {
            if (!this.modalEvent.isAcknowledged()) {
                this.modalEvent.reply(message).queue();
            } else {
                this.modalEvent.getHook().sendMessage(message).queue();
            }
        }
    }

    @Override
    public void replyEphemeral(String message) {
        if (this.slashEvent != null) {
            if (!this.slashEvent.isAcknowledged()) {
                this.slashEvent.reply(message).setEphemeral(true).queue();
            } else {
                this.slashEvent.getHook().sendMessage(message).setEphemeral(true).queue();
            }
        } else if (this.buttonEvent != null) {
            if (!this.buttonEvent.isAcknowledged()) {
                this.buttonEvent.reply(message).setEphemeral(true).queue();
            } else {
                this.buttonEvent.getHook().sendMessage(message).setEphemeral(true).queue();
            }
        } else if (this.modalEvent != null) {
            if (!this.modalEvent.isAcknowledged()) {
                this.modalEvent.reply(message).setEphemeral(true).queue();
            } else {
                this.modalEvent.getHook().sendMessage(message).setEphemeral(true).queue();
            }
        }
    }

    @Override
    public void replyEmbed(Object embed) {
        if (embed instanceof MessageEmbed) {
            if (this.slashEvent != null) {
                this.slashEvent.replyEmbeds((MessageEmbed) embed).queue();
            } else if (this.buttonEvent != null) {
                this.buttonEvent.replyEmbeds((MessageEmbed) embed).queue();
            } else if (this.modalEvent != null) {
                this.modalEvent.replyEmbeds((MessageEmbed) embed).queue();
            }
        }
    }

    @Override
    public void deferReply() {
        if (this.slashEvent != null) {
            this.slashEvent.deferReply().queue();
        } else if (this.buttonEvent != null) {
            this.buttonEvent.deferReply().queue();
        } else if (this.modalEvent != null) {
            this.modalEvent.deferReply().queue();
        }
    }

    @Override
    public void deferReplyEphemeral() {
        if (this.slashEvent != null) {
            this.slashEvent.deferReply(true).queue();
        } else if (this.buttonEvent != null) {
            this.buttonEvent.deferReply(true).queue();
        } else if (this.modalEvent != null) {
            this.modalEvent.deferReply(true).queue();
        }
    }

    @Override
    public Object getUser() {
        if (slashEvent != null) return slashEvent.getUser();
        if (buttonEvent != null) return buttonEvent.getUser();
        if (modalEvent != null) return modalEvent.getUser();
        return null;
    }

    @Override
    public Object getMember() {
        if (slashEvent != null) return slashEvent.getMember();
        if (buttonEvent != null) return buttonEvent.getMember();
        if (modalEvent != null) return modalEvent.getMember();
        return null;
    }

    @Override
    public Object getGuild() {
        if (slashEvent != null) return slashEvent.getGuild();
        if (buttonEvent != null) return buttonEvent.getGuild();
        if (modalEvent != null) return modalEvent.getGuild();
        return null;
    }

    @Override
    public Object getChannel() {
        if (slashEvent != null) return slashEvent.getChannel();
        if (buttonEvent != null) return buttonEvent.getChannel();
        if (modalEvent != null) return modalEvent.getChannel();
        return null;
    }
    
    public User getUserTyped() {
        if (slashEvent != null) return slashEvent.getUser();
        if (buttonEvent != null) return buttonEvent.getUser();
        if (modalEvent != null) return modalEvent.getUser();
        return null;
    }

    public Member getMemberTyped() {
        if (slashEvent != null) return slashEvent.getMember();
        if (buttonEvent != null) return buttonEvent.getMember();
        if (modalEvent != null) return modalEvent.getMember();
        return null;
    }

    public Guild getGuildTyped() {
        if (slashEvent != null) return slashEvent.getGuild();
        if (buttonEvent != null) return buttonEvent.getGuild();
        if (modalEvent != null) return modalEvent.getGuild();
        return null;
    }

    public MessageChannel getChannelTyped() {
        if (slashEvent != null) return slashEvent.getChannel();
        if (buttonEvent != null) return buttonEvent.getChannel();
        if (modalEvent != null) return modalEvent.getChannel();
        return null;
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
        Member member = getMemberTyped();
        if (member == null) return false;
        try {
            Permission perm = Permission.valueOf(permission);
            return member.hasPermission(perm);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public SlashCommandInteractionEvent getSlashEvent() {
        return slashEvent;
    }

    public ButtonInteractionEvent getButtonEvent() {
        return buttonEvent;
    }

    public ModalInteractionEvent getModalEvent() {
        return modalEvent;
    }
}
