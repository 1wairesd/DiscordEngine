package com.wairesdindustries.discordengine.common.discord.flow.actions;

import com.wairesdindustries.discordengine.common.discord.flow.FlowAction;
import com.wairesdindustries.discordengine.common.discord.flow.FlowContext;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class AddRoleAction implements FlowAction {
    private final String roleId;

    public AddRoleAction(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public void execute(FlowContext context) throws Exception {
        Guild guild = context.getInteraction().getGuild();
        if (guild == null) {
            throw new IllegalStateException("Cannot add role outside of a guild");
        }

        Member member = context.getInteraction().getMember();
        if (member == null) {
            throw new IllegalStateException("Cannot add role to null member");
        }

        Role role = guild.getRoleById(roleId);
        if (role == null) {
            throw new IllegalArgumentException("Role not found: " + roleId);
        }

        guild.addRoleToMember(member, role).queue();
    }
}