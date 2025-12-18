package com.wairesdindustries.discordengine.common.discord.flow;

import com.wairesdindustries.discordengine.common.DiscordEngine;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class FlowDispatcher {
    private final DiscordEngine api;
    private final FlowRegistry flowRegistry;
    private final EntryPointRegistry entryPointRegistry;
    private final FlowExecutor flowExecutor;

    public FlowDispatcher(DiscordEngine api, FlowRegistry flowRegistry, 
                         EntryPointRegistry entryPointRegistry, FlowExecutor flowExecutor) {
        this.api = api;
        this.flowRegistry = flowRegistry;
        this.entryPointRegistry = entryPointRegistry;
        this.flowExecutor = flowExecutor;
    }

    public void handleSlashCommand(SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        String flowId = entryPointRegistry.getFlowForCommand(commandName);
        
        if (flowId == null) {
            event.reply("Command not configured").setEphemeral(true).queue();
            return;
        }

        Flow flow = flowRegistry.getFlow(flowId);
        if (flow == null) {
            event.reply("Flow not found").setEphemeral(true).queue();
            return;
        }

        FlowContext context = new FlowContext(event);
        flowExecutor.executeFlow(flow, context);
    }

    public void handleButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        String flowId = entryPointRegistry.getFlowForButton(buttonId);
        
        if (flowId == null) {
            event.reply("Button not configured").setEphemeral(true).queue();
            return;
        }

        Flow flow = flowRegistry.getFlow(flowId);
        if (flow == null) {
            event.reply("Flow not found").setEphemeral(true).queue();
            return;
        }

        FlowContext context = new FlowContext(event);
        flowExecutor.executeFlow(flow, context);
    }

    public void handleModalInteraction(ModalInteractionEvent event) {
        String modalId = event.getModalId();

        var modalRegistry = api.getFlowManager().getModalRegistry();
        var modalDef = modalRegistry.getModal(modalId);
        
        String flowId;
        String stepId;
        
        if (modalDef != null && modalDef.getOnSubmit() != null) {
            flowId = modalDef.getOnSubmit().getFlowId();
            stepId = modalDef.getOnSubmit().getStepId();
        } else {
            flowId = modalId.startsWith("user_") ? modalId.substring(5) : modalId;
            stepId = "finish";
        }
        
        Flow flow = flowRegistry.getFlow(flowId);
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