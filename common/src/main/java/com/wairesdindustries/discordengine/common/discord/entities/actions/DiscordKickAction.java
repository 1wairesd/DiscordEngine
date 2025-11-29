package com.wairesdindustries.discordengine.common.discord.entities.actions;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class DiscordKickAction implements DiscordAction {

    private final String targetOption;
    private final String reason;

    public DiscordKickAction(String targetOption, String reason) {
        this.targetOption = targetOption;
        this.reason = reason;
    }

    @Override
    public void execute(DiscordCommandContext context) {
        if (!(context instanceof com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl impl)) {
            return;
        }

        if (!context.hasPermission(Permission.KICK_MEMBERS.name())) {
            context.replyEphemeral("You don't have permission to kick members!");
            return;
        }

        Guild guild = impl.getGuildTyped();
        if (guild == null) {
            context.replyEphemeral("This command can only be used in a server!");
            return;
        }

        Member target = impl.getOptionAsMemberTyped(targetOption);
        if (target == null) {
            context.replyEphemeral("Could not find target member!");
            return;
        }

        String kickReason = reason != null ? reason : "No reason provided";

        guild.kick(target).reason(kickReason).queue(
                success -> context.reply("Kicked " + target.getEffectiveName() + " for: " + kickReason),
                error -> context.replyEphemeral("Failed to kick member: " + error.getMessage())
        );
    }
}
