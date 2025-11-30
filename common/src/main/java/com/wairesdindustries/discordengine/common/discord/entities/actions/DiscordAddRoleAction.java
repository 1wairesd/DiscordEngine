package com.wairesdindustries.discordengine.common.discord.entities.actions;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class DiscordAddRoleAction implements DiscordAction {

    private final String roleId;
    private final String targetOption;

    public DiscordAddRoleAction(String roleId, String targetOption) {
        this.roleId = roleId;
        this.targetOption = targetOption;
    }

    @Override
    public void execute(DiscordCommandContext context) {
        if (!(context instanceof com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl impl)) {
            return;
        }

        Guild guild = impl.getGuildTyped();
        if (guild == null) {
            context.replyEphemeral("This command can only be used in a server!");
            return;
        }

        Member target = targetOption != null ? impl.getOptionAsMemberTyped(targetOption) : impl.getMemberTyped();

        if (target == null) {
            context.replyEphemeral("Could not find target member!");
            return;
        }

        Role role = guild.getRoleById(roleId);
        if (role == null) {
            context.replyEphemeral("Role not found!");
            return;
        }

        // Silently add role without sending a message (other actions may have already replied)
        guild.addRoleToMember(target, role).queue();
    }
}
