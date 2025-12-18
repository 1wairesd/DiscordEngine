package com.wairesdindustries.discordengine.common.discord.flow;

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.Interaction;

public class FlowContext {
    private final Interaction interaction;
    private final Map<String, String> modalInputs;
    private final Map<String, Object> variables;

    public FlowContext(Interaction interaction) {
        this.interaction = interaction;
        this.modalInputs = new HashMap<>();
        this.variables = new HashMap<>();
    }

    public FlowContext(net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent event) {
        this.interaction = event.getInteraction();
        this.modalInputs = new HashMap<>();
        this.variables = new HashMap<>();
    }

    public FlowContext(net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent event) {
        this.interaction = event.getInteraction();
        this.modalInputs = new HashMap<>();
        this.variables = new HashMap<>();
    }

    public FlowContext(ModalInteractionEvent event) {
        this.interaction = event.getInteraction();
        this.modalInputs = new HashMap<>();
        this.variables = new HashMap<>();

        event.getValues().forEach(mapping -> 
            modalInputs.put(mapping.getId(), mapping.getAsString())
        );
    }

    public Interaction getInteraction() {
        return interaction;
    }

    public String getModalInput(String inputId) {
        return modalInputs.get(inputId);
    }

    public Map<String, String> getAllModalInputs() {
        return new HashMap<>(modalInputs);
    }

    public void setVariable(String key, Object value) {
        variables.put(key, value);
    }

    public Object getVariable(String key) {
        return variables.get(key);
    }

    public <T> T getVariable(String key, Class<T> type) {
        Object value = variables.get(key);
        if (value != null && type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    public boolean isSlashCommand() {
        return interaction instanceof net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
    }

    public boolean isButtonInteraction() {
        return interaction instanceof net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
    }

    public boolean isModalInteraction() {
        return interaction instanceof net.dv8tion.jda.api.interactions.modals.ModalInteraction;
    }

    public net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction asSlashCommand() {
        if (isSlashCommand()) {
            return (net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction) interaction;
        }
        throw new IllegalStateException("Context is not a slash command interaction");
    }

    public net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction asButtonInteraction() {
        if (isButtonInteraction()) {
            return (net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction) interaction;
        }
        throw new IllegalStateException("Context is not a button interaction");
    }

    public net.dv8tion.jda.api.interactions.modals.ModalInteraction asModalInteraction() {
        if (isModalInteraction()) {
            return (net.dv8tion.jda.api.interactions.modals.ModalInteraction) interaction;
        }
        throw new IllegalStateException("Context is not a modal interaction");
    }
}