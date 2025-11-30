package com.wairesdindustries.discordengine.common.discord.entities.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wairesdindustries.discordengine.api.discord.component.ComponentReference;
import com.wairesdindustries.discordengine.api.discord.component.DiscordComponent;
import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;
import com.wairesdindustries.discordengine.common.DiscordEngine;
import com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl;

import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class DiscordComponentReferenceAction implements DiscordAction {
    private final DiscordEngine api;
    private final String reference;

    public DiscordComponentReferenceAction(DiscordEngine api, String reference) {
        this.api = api;
        this.reference = reference;
    }

    @Override
    public void execute(DiscordCommandContext context) {
        try {
            ComponentReference ref = new ComponentReference(reference);
            DiscordComponent component = api.getComponentLoader().getComponent(ref);

            if (component == null) {
                context.replyEphemeral("Component not found: " + reference);
                return;
            }

            if (!checkPermissions(context, component)) {
                context.replyEphemeral("You don't have permission to use this component");
                return;
            }

            switch (component.getType()) {
                case BUTTON -> executeButtonComponent(context, component);
                case MODAL -> executeModalComponent(context, component);
                case MESSAGE -> executeMessageComponent(context, component);
                default -> context.replyEphemeral("Unsupported component type: " + component.getType());
            }
        } catch (Exception e) {
            context.replyEphemeral("Error executing component: " + e.getMessage());
            api.getPlatform().getLogger().severe("Component execution error: " + e.getMessage());
        }
    }

    private boolean checkPermissions(DiscordCommandContext context, DiscordComponent component) {
        if (component.getRequiredPermissions() == null || component.getRequiredPermissions().isEmpty()) {
            return true;
        }

        for (String permission : component.getRequiredPermissions()) {
            if (!context.hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    private void executeButtonComponent(DiscordCommandContext context, DiscordComponent component) {
        String message = component.getPropertyAsString("message");
        List<Map<String, Object>> buttonsData = component.getPropertyAsList("buttons");

        if (buttonsData == null || buttonsData.isEmpty()) {
            context.reply(message != null ? message : "No buttons configured");
            return;
        }

        List<Button> buttons = new ArrayList<>();
        for (Map<String, Object> buttonData : buttonsData) {
            Button button = createButton(buttonData);
            if (button != null) {
                buttons.add(button);
            }
        }

        if (context instanceof DiscordCommandContextImpl impl) {
            if (impl.getSlashEvent() != null) {
                var event = impl.getSlashEvent();
                if (!event.isAcknowledged()) {
                    event.reply(message != null ? message : "Choose an option:")
                        .addActionRow(buttons)
                        .queue();
                } else {
                    event.getHook().sendMessage(message != null ? message : "Choose an option:")
                        .addActionRow(buttons)
                        .queue();
                }
            }
            else if (impl.getButtonEvent() != null) {
                var event = impl.getButtonEvent();
                if (!event.isAcknowledged()) {
                    event.reply(message != null ? message : "Choose an option:")
                        .addActionRow(buttons)
                        .queue();
                } else {
                    event.getHook().sendMessage(message != null ? message : "Choose an option:")
                        .addActionRow(buttons)
                        .queue();
                }
            }
        }
    }

    private Button createButton(Map<String, Object> data) {
        String label = (String) data.getOrDefault("label", "Button");
        String id = (String) data.get("id");
        String style = (String) data.getOrDefault("style", "PRIMARY");
        String emoji = (String) data.get("emoji");
        String url = (String) data.get("url");
        String reference = (String) data.get("reference");

        String customId = reference != null ? "ref:" + reference : (id != null ? id : "btn_" + System.currentTimeMillis());

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

        return button;
    }

    private void executeModalComponent(DiscordCommandContext context, DiscordComponent component) {
        String title = component.getPropertyAsString("title");
        List<Map<String, Object>> inputsData = component.getPropertyAsList("inputs");

        if (inputsData == null || inputsData.isEmpty()) {
            context.replyEphemeral("No inputs configured for modal");
            return;
        }

        Modal.Builder modalBuilder = Modal.create(
            "modal:" + reference,
            title != null ? title : "Input Form"
        );

        for (Map<String, Object> inputData : inputsData) {
            TextInput input = createTextInput(inputData);
            if (input != null) {
                modalBuilder.addActionRow(input);
            }
        }

        if (context instanceof DiscordCommandContextImpl impl) {
            if (impl.getSlashEvent() != null) {
                var event = impl.getSlashEvent();
                if (!event.isAcknowledged()) {
                    event.replyModal(modalBuilder.build()).queue();
                } else {
                    context.replyEphemeral("Cannot open modal - interaction already acknowledged");
                }
            } else if (impl.getButtonEvent() != null) {
                var event = impl.getButtonEvent();
                if (!event.isAcknowledged()) {
                    event.replyModal(modalBuilder.build()).queue();
                } else {
                    context.replyEphemeral("Cannot open modal - interaction already acknowledged");
                }
            } else {
                context.replyEphemeral("Cannot open modal - unsupported interaction type");
            }
        }
    }

    private TextInput createTextInput(Map<String, Object> data) {
        String inputId = (String) data.getOrDefault("id", "input_" + System.currentTimeMillis());
        String label = (String) data.getOrDefault("label", "Input");
        String style = (String) data.getOrDefault("style", "SHORT");
        String placeholder = (String) data.get("placeholder");
        String value = (String) data.get("value");
        Boolean required = (Boolean) data.getOrDefault("required", true);

        TextInputStyle inputStyle = "PARAGRAPH".equalsIgnoreCase(style)
            ? TextInputStyle.PARAGRAPH
            : TextInputStyle.SHORT;

        TextInput.Builder builder = TextInput.create(inputId, label, inputStyle)
            .setRequired(required);

        if (placeholder != null && !placeholder.isEmpty()) {
            builder.setPlaceholder(placeholder);
        }

        if (value != null && !value.isEmpty()) {
            builder.setValue(value);
        }

        return builder.build();
    }

    private void executeMessageComponent(DiscordCommandContext context, DiscordComponent component) {
        String content = component.getPropertyAsString("content");
        if (content != null) {
            context.reply(content);
        }
    }

    private ButtonStyle parseButtonStyle(String style) {
        if (style == null) return ButtonStyle.PRIMARY;
        
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
