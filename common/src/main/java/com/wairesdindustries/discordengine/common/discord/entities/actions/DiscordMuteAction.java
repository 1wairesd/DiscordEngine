package com.wairesdindustries.discordengine.common.discord.entities.actions;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

public class DiscordMuteAction implements DiscordAction {

    private final String targetOption;
    private final boolean mute;

    public DiscordMuteAction(String targetOption, boolean mute) {
        this.targetOption = targetOption;
        this.mute = mute;
    }

    @Override
    public void execute(DiscordCommandContext context) {
        if (!(context instanceof com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl impl)) {
            return;
        }

        if (!context.hasPermission(Permission.VOICE_MUTE_OTHERS.name())) {
            context.replyEphemeral("You don't have permission to mute members!");
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

        target.mute(mute).queue(
                success -> context.reply((mute ? "Muted " : "Unmuted ") + target.getEffectiveName()),
                error -> context.replyEphemeral("Failed to " + (mute ? "mute" : "unmute") + " member: " + error.getMessage())
        );
    }
}
