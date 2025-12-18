package com.wairesdindustries.discordengine.common.discord.flow.actions;

import java.util.ArrayList;
import java.util.List;

import com.wairesdindustries.discordengine.common.discord.flow.FlowAction;
import com.wairesdindustries.discordengine.common.discord.flow.FlowContext;
import com.wairesdindustries.discordengine.common.discord.flow.modal.ModalDefinition;
import com.wairesdindustries.discordengine.common.discord.flow.modal.ModalRegistryImpl;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class OpenModalAction implements FlowAction {
    private final String modalId;
    private final ModalRegistryImpl modalRegistry;

    public OpenModalAction(String modalId, ModalRegistryImpl modalRegistry) {
        this.modalId = modalId;
        this.modalRegistry = modalRegistry;
    }

    @Override
    public void execute(FlowContext context) throws Exception {
        ModalDefinition modalDef = modalRegistry.getModalTyped(modalId);
        if (modalDef == null) {
            throw new IllegalArgumentException("Modal not found: " + modalId);
        }

        List<ActionRow> components = new ArrayList<>();
        
        for (ModalDefinition.Input input : modalDef.getInputs()) {
            TextInputStyle style = input.getStyle().equals("PARAGRAPH") 
                ? TextInputStyle.PARAGRAPH 
                : TextInputStyle.SHORT;
                
            TextInput textInput = TextInput.create(input.getId(), input.getLabel(), style)
                .setPlaceholder(input.getPlaceholder())
                .setRequired(input.isRequired())
                .build();
                
            components.add(ActionRow.of(textInput));
        }

        Modal modal = Modal.create(modalId, modalDef.getTitle())
            .addComponents(components)
            .build();

        if (context.isSlashCommand()) {
            context.asSlashCommand().replyModal(modal).queue();
        } else if (context.isButtonInteraction()) {
            context.asButtonInteraction().replyModal(modal).queue();
        } else {
            throw new IllegalStateException("Modal can only be opened from slash commands or button interactions");
        }
    }
}