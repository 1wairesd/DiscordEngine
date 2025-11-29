package com.wairesdindustries.discordengine.common.discord.entities.actions;

import java.awt.Color;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;

import net.dv8tion.jda.api.Permission;

public class DiscordCreateRoleAction implements DiscordAction {

    private final String roleName;
    private final String color;
    private final boolean hoisted;
    private final boolean mentionable;

    public DiscordCreateRoleAction(String roleName, String color, boolean hoisted, boolean mentionable) {
        this.roleName = roleName;
        this.color = color;
        this.hoisted = hoisted;
        this.mentionable = mentionable;
    }

    @Override
    public void execute(DiscordCommandContext context) {
        if (!(context instanceof com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl impl)) {
            return;
        }

        if (!context.hasPermission(Permission.MANAGE_ROLES.name())) {
            context.replyEphemeral("You don't have permission to manage roles!");
            return;
        }

        if (impl.getGuildTyped() == null) {
            context.replyEphemeral("This command can only be used in a server!");
            return;
        }

        impl.getGuildTyped().createRole()
                .setName(roleName)
                .setColor(parseColor(color))
                .setHoisted(hoisted)
                .setMentionable(mentionable)
                .queue(
                        role -> context.reply("Role created: " + role.getName()),
                        error -> context.replyEphemeral("Failed to create role: " + error.getMessage())
                );
    }

    private Color parseColor(String colorStr) {
        if (colorStr == null || colorStr.isEmpty()) {
            return null;
        }
        try {
            return Color.decode(colorStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
