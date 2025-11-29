package com.wairesdindustries.discordengine.common.discord.entities.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;
import com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl;

import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public class DiscordButtonAction implements DiscordAction {

    private final String message;
    private final List<Map<String, String>> buttons;

    public DiscordButtonAction(String message, List<Map<String, String>> buttons) {
        this.message = message;
        this.buttons = buttons;
    }

    @Override
    public void execute(DiscordCommandContext context) {
        if (buttons == null || buttons.isEmpty()) {
            context.reply(message != null ? message : "No buttons configured");
            return;
        }

        List<Button> buttonList = new ArrayList<>();
        for (Map<String, String> buttonData : buttons) {
            String label = buttonData.getOrDefault("label", "Button");
            String customId = buttonData.getOrDefault("id", "button_" + System.currentTimeMillis());
            String style = buttonData.getOrDefault("style", "PRIMARY");
            String emoji = buttonData.get("emoji");
            String url = buttonData.get("url");

            ButtonStyle buttonStyle = parseButtonStyle(style);
            
            Button button;
            if (url != null && !url.isEmpty()) {
                button = Button.link(url, label);
            } else {
                button = Button.of(buttonStyle, customId, label);
            }
            
            if (emoji != null && !emoji.isEmpty()) {
                try {
                    button = button.withEmoji(net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode(emoji));
                } catch (Exception ignored) {}
            }
            
            buttonList.add(button);
        }

        if (context instanceof DiscordCommandContextImpl impl) {
            impl.getSlashEvent().reply(message != null ? message : "Choose an option:")
                    .addActionRow(buttonList)
                    .queue();
        }
    }

    private ButtonStyle parseButtonStyle(String style) {
        switch (style.toUpperCase()) {
            case "SUCCESS":
            case "GREEN":
                return ButtonStyle.SUCCESS;
            case "DANGER":
            case "RED":
                return ButtonStyle.DANGER;
            case "SECONDARY":
            case "GRAY":
            case "GREY":
                return ButtonStyle.SECONDARY;
            case "PRIMARY":
            case "BLUE":
            default:
                return ButtonStyle.PRIMARY;
        }
    }
}
