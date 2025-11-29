package com.wairesdindustries.discordengine.common.discord.entities.actions;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

public class DiscordDeafenAction implements DiscordAction {

    private final String targetOption;
    private final boolean deafen;

    public DiscordDeafenAction(String targetOption, boolean deafen) {
        this.targetOption = targetOption;
        this.deafen = deafen;
    }

    @Override
    public void execute(DiscordCommandContext context) {
        if (!(context instanceof com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl impl)) {
            return;
        }

        if (!context.hasPermission(Permission.VOICE_DEAF_OTHERS.name())) {
            context.replyEphemeral("You don't have permission to deafen members!");
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

        target.deafen(deafen).queue(
                success -> context.reply((deafen ? "Deafened " : "Undeafened ") + target.getEffectiveName()),
                error -> context.replyEphemeral("Failed to " + (deafen ? "deafen" : "undeafen") + " member: " + error.getMessage())
        );
    }
}
