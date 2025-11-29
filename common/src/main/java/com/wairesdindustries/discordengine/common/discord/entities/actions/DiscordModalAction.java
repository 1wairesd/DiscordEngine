package com.wairesdindustries.discordengine.common.discord.entities.actions;

import java.util.List;
import java.util.Map;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;
import com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl;

import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class DiscordModalAction implements DiscordAction {

    private final String modalId;
    private final String title;
    private final List<Map<String, String>> inputs;

    public DiscordModalAction(String modalId, String title, List<Map<String, String>> inputs) {
        this.modalId = modalId;
        this.title = title;
        this.inputs = inputs;
    }

    @Override
    public void execute(DiscordCommandContext context) {
        if (inputs == null || inputs.isEmpty()) {
            context.replyEphemeral("No inputs configured for modal");
            return;
        }

        Modal.Builder modalBuilder = Modal.create(
            modalId != null ? modalId : "modal_" + System.currentTimeMillis(),
            title != null ? title : "Input Form"
        );

        for (Map<String, String> inputData : inputs) {
            String id = inputData.getOrDefault("id", "input_" + System.currentTimeMillis());
            String label = inputData.getOrDefault("label", "Input");
            String style = inputData.getOrDefault("style", "SHORT");
            String placeholder = inputData.get("placeholder");
            String value = inputData.get("value");
            boolean required = Boolean.parseBoolean(inputData.getOrDefault("required", "true"));
            
            TextInputStyle inputStyle = "PARAGRAPH".equalsIgnoreCase(style) 
                ? TextInputStyle.PARAGRAPH 
                : TextInputStyle.SHORT;

            TextInput.Builder inputBuilder = TextInput.create(id, label, inputStyle)
                .setRequired(required);
            
            if (placeholder != null && !placeholder.isEmpty()) {
                inputBuilder.setPlaceholder(placeholder);
            }
            
            if (value != null && !value.isEmpty()) {
                inputBuilder.setValue(value);
            }
            
            modalBuilder.addActionRow(inputBuilder.build());
        }

        if (context instanceof DiscordCommandContextImpl impl) {
            impl.getSlashEvent().replyModal(modalBuilder.build()).queue();
        }
    }
}
