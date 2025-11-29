package com.wairesdindustries.discordengine.common.discord.entities.actions;

import java.util.List;
import java.util.Map;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;
import com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl;

import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

public class DiscordSelectMenuAction implements DiscordAction {

    private final String message;
    private final String menuId;
    private final String placeholder;
    private final List<Map<String, String>> options;

    public DiscordSelectMenuAction(String message, String menuId, String placeholder, List<Map<String, String>> options) {
        this.message = message;
        this.menuId = menuId;
        this.placeholder = placeholder;
        this.options = options;
    }

    @Override
    public void execute(DiscordCommandContext context) {
        if (options == null || options.isEmpty()) {
            context.reply(message != null ? message : "No options configured");
            return;
        }

        StringSelectMenu.Builder menuBuilder = StringSelectMenu.create(menuId != null ? menuId : "menu_" + System.currentTimeMillis());
        
        if (placeholder != null && !placeholder.isEmpty()) {
            menuBuilder.setPlaceholder(placeholder);
        }

        for (Map<String, String> optionData : options) {
            String label = optionData.getOrDefault("label", "Option");
            String value = optionData.getOrDefault("value", label);
            String description = optionData.get("description");
            String emoji = optionData.get("emoji");

            var option = net.dv8tion.jda.api.interactions.components.selections.SelectOption.of(label, value);
            
            if (description != null && !description.isEmpty()) {
                option = option.withDescription(description);
            }
            
            if (emoji != null && !emoji.isEmpty()) {
                try {
                    option = option.withEmoji(net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode(emoji));
                } catch (Exception ignored) {}
            }
            
            menuBuilder.addOptions(option);
        }

        if (context instanceof DiscordCommandContextImpl impl) {
            impl.getSlashEvent().reply(message != null ? message : "Select an option:")
                    .addActionRow(menuBuilder.build())
                    .queue();
        }
    }
}
