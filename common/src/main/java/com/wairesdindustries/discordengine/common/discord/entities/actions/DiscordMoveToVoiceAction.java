package com.wairesdindustries.discordengine.common.discord.entities.actions;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public class DiscordMoveToVoiceAction implements DiscordAction {

    private final String targetOption;
    private final String voiceChannelId;

    public DiscordMoveToVoiceAction(String targetOption, String voiceChannelId) {
        this.targetOption = targetOption;
        this.voiceChannelId = voiceChannelId;
    }

    @Override
    public void execute(DiscordCommandContext context) {
        if (!(context instanceof com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl impl)) {
            return;
        }

        if (!context.hasPermission(Permission.VOICE_MOVE_OTHERS.name())) {
            context.replyEphemeral("You don't have permission to move members!");
            return;
        }

        if (impl.getGuildTyped() == null) {
            context.replyEphemeral("This command can only be used in a server!");
            return;
        }

        Member target = impl.getOptionAsMemberTyped(targetOption);
        if (target == null) {
            context.replyEphemeral("Could not find target member!");
            return;
        }

        VoiceChannel voiceChannel = impl.getGuildTyped().getVoiceChannelById(voiceChannelId);
        if (voiceChannel == null) {
            context.replyEphemeral("Voice channel not found!");
            return;
        }

        impl.getGuildTyped().moveVoiceMember(target, voiceChannel).queue(
                success -> context.reply("Moved " + target.getEffectiveName() + " to " + voiceChannel.getName()),
                error -> context.replyEphemeral("Failed to move member: " + error.getMessage())
        );
    }
}
