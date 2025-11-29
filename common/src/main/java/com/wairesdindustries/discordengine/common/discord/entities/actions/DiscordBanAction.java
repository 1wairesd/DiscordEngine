package com.wairesdindustries.discordengine.common.discord.entities.actions;

import java.util.concurrent.TimeUnit;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class DiscordBanAction implements DiscordAction {

    private final String targetOption;
    private final String reason;
    private final int deleteMessageDays;

    public DiscordBanAction(String targetOption, String reason, int deleteMessageDays) {
        this.targetOption = targetOption;
        this.reason = reason;
        this.deleteMessageDays = Math.min(Math.max(deleteMessageDays, 0), 7);
    }

    @Override
    public void execute(DiscordCommandContext context) {
        if (!(context instanceof com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl impl)) {
            return;
        }

        if (!context.hasPermission(Permission.BAN_MEMBERS.name())) {
            context.replyEphemeral("You don't have permission to ban members!");
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

        String banReason = reason != null ? reason : "No reason provided";

        guild.ban(target, deleteMessageDays, TimeUnit.DAYS).reason(banReason).queue(
                success -> context.reply("Banned " + target.getEffectiveName() + " for: " + banReason),
                error -> context.replyEphemeral("Failed to ban member: " + error.getMessage())
        );
    }
}
