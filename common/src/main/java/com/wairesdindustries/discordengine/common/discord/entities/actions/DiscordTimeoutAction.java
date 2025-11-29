package com.wairesdindustries.discordengine.common.discord.entities.actions;

import java.time.Duration;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

public class DiscordTimeoutAction implements DiscordAction {

    private final String targetOption;
    private final long durationMinutes;
    private final String reason;

    public DiscordTimeoutAction(String targetOption, long durationMinutes, String reason) {
        this.targetOption = targetOption;
        this.durationMinutes = durationMinutes;
        this.reason = reason;
    }

    @Override
    public void execute(DiscordCommandContext context) {
        if (!(context instanceof com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl impl)) {
            return;
        }

        if (!context.hasPermission(Permission.MODERATE_MEMBERS.name())) {
            context.replyEphemeral("You don't have permission to timeout members!");
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

        String timeoutReason = reason != null ? reason : "No reason provided";
        Duration duration = Duration.ofMinutes(durationMinutes);

        target.timeoutFor(duration).reason(timeoutReason).queue(
                success -> context.reply("Timed out " + target.getEffectiveName() + " for " + durationMinutes + " minutes. Reason: " + timeoutReason),
                error -> context.replyEphemeral("Failed to timeout member: " + error.getMessage())
        );
    }
}
