package com.wairesdindustries.discordengine.common.discord.flow;

import com.wairesdindustries.discordengine.api.discord.flow.FlowDispatcher;
import com.wairesdindustries.discordengine.common.DiscordEngine;
import com.wairesdindustries.discordengine.common.discord.flow.modal.ModalRegistryImpl;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class FlowDispatcherImpl implements FlowDispatcher {
    private final DiscordEngine api;
    private final FlowRegistryImpl flowRegistry;
    private final EntryPointRegistryImpl entryPointRegistry;
    private final FlowExecutor flowExecutor;

    public FlowDispatcherImpl(DiscordEngine api, FlowRegistryImpl flowRegistry,
                              EntryPointRegistryImpl entryPointRegistry, FlowExecutor flowExecutor) {
        this.api = api;
        this.flowRegistry = flowRegistry;
        this.entryPointRegistry = entryPointRegistry;
        this.flowExecutor = flowExecutor;
    }

    @Override
    public void handleSlashCommand(Object event) {
        if (!(event instanceof SlashCommandInteractionEvent)) {
            throw new IllegalArgumentException("Expected SlashCommandInteractionEvent");
        }
        handleSlashCommand((SlashCommandInteractionEvent) event);
    }

    public void handleSlashCommand(SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        CommandDefinition commandDef = entryPointRegistry.getCommandDefinition(commandName);
        
        if (commandDef == null) {
            event.reply("Command not configured").setEphemeral(true).queue();
            return;
        }

        boolean isGuild = event.getGuild() != null;
        boolean isDM = !isGuild;
        
        CommandDefinition.CommandScope scope = commandDef.getScope();
        if ((scope == CommandDefinition.CommandScope.GUILD && isDM) ||
            (scope == CommandDefinition.CommandScope.DM && isGuild)) {
            
            String scopeMessage = scope == CommandDefinition.CommandScope.GUILD 
                ? "This command can only be used in servers." 
                : "This command can only be used in direct messages.";
            event.reply(scopeMessage).setEphemeral(true).queue();
            return;
        }

        String flowId = commandDef.getFlowId();

        Flow flow = flowRegistry.getFlowTyped(flowId);
        if (flow == null) {
            event.reply("Flow not found").setEphemeral(true).queue();
            return;
        }

        FlowContext context = new FlowContext(event);
        flowExecutor.executeFlow(flow, context);
    }

    @Override
    public void handleButtonInteraction(Object event) {
        if (!(event instanceof ButtonInteractionEvent)) {
            throw new IllegalArgumentException("Expected ButtonInteractionEvent");
        }
        handleButtonInteraction((ButtonInteractionEvent) event);
    }

    public void handleButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        String flowId = entryPointRegistry.getFlowForButton(buttonId);
        
        if (flowId == null) {
            event.reply("Button not configured").setEphemeral(true).queue();
            return;
        }

        Flow flow = flowRegistry.getFlowTyped(flowId);
        if (flow == null) {
            event.reply("Flow not found").setEphemeral(true).queue();
            return;
        }

        FlowContext context = new FlowContext(event);
        flowExecutor.executeFlow(flow, context);
    }

    @Override
    public void handleModalInteraction(Object event) {
        if (!(event instanceof ModalInteractionEvent)) {
            throw new IllegalArgumentException("Expected ModalInteractionEvent");
        }
        handleModalInteraction((ModalInteractionEvent) event);
    }

    public void handleModalInteraction(ModalInteractionEvent event) {
        String modalId = event.getModalId();

        var modalRegistry = (ModalRegistryImpl) api.getFlowManager().getModalRegistry();
        var modalDef = modalRegistry.getModalTyped(modalId);
        
        String flowId;
        String stepId;
        
        if (modalDef != null && modalDef.getOnSubmit() != null) {
            flowId = modalDef.getOnSubmit().getFlowId();
            stepId = modalDef.getOnSubmit().getStepId();
        } else {
            flowId = modalId.startsWith("user_") ? modalId.substring(5) : modalId;
            stepId = "finish";
        }
        
        Flow flow = flowRegistry.getFlowTyped(flowId);
        if (flow == null) {
            event.reply("Modal processing failed").setEphemeral(true).queue();
            return;
        }

        FlowContext context = new FlowContext(event);

        if (flow.getStep(stepId) != null) {
            flowExecutor.executeFromStep(flow, stepId, context);
        } else {
            flowExecutor.executeFlow(flow, context);
        }
    }
}