package com.wairesdindustries.discordengine.common.discord.entities.actions;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

public class DiscordNicknameAction implements DiscordAction {

    private final String targetOption;
    private final String nickname;

    public DiscordNicknameAction(String targetOption, String nickname) {
        this.targetOption = targetOption;
        this.nickname = nickname;
    }

    @Override
    public void execute(DiscordCommandContext context) {
        if (!(context instanceof com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl impl)) {
            return;
        }

        if (!context.hasPermission(Permission.NICKNAME_MANAGE.name())) {
            context.replyEphemeral("You don't have permission to manage nicknames!");
            return;
        }

        if (impl.getGuildTyped() == null) {
            context.replyEphemeral("This command can only be used in a server!");
            return;
        }

        Member target = targetOption != null ? impl.getOptionAsMemberTyped(targetOption) : impl.getMemberTyped();

        if (target == null) {
            context.replyEphemeral("Could not find target member!");
            return;
        }

        target.modifyNickname(nickname).queue(
                success -> context.reply("Changed nickname of " + target.getUser().getName() + " to " + (nickname != null ? nickname : "default")),
                error -> context.replyEphemeral("Failed to change nickname: " + error.getMessage())
        );
    }
}
