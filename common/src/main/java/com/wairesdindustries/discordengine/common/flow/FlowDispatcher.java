package com.wairesdindustries.discordengine.common.flow;

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
            api.getPlatform().getLogger().warning("No flow mapped for command: " + commandName);
            event.reply("Command not configured").setEphemeral(true).queue();
            return;
        }

        Flow flow = flowRegistry.getFlow(flowId);
        if (flow == null) {
            api.getPlatform().getLogger().warning("Flow not found: " + flowId + " for command: " + commandName);
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
            api.getPlatform().getLogger().warning("No flow mapped for button: " + buttonId);
            event.reply("Button not configured").setEphemeral(true).queue();
            return;
        }

        Flow flow = flowRegistry.getFlow(flowId);
        if (flow == null) {
            api.getPlatform().getLogger().warning("Flow not found: " + flowId + " for button: " + buttonId);
            event.reply("Flow not found").setEphemeral(true).queue();
            return;
        }

        FlowContext context = new FlowContext(event);
        flowExecutor.executeFlow(flow, context);
    }

    public void handleModalInteraction(ModalInteractionEvent event) {
        String modalId = event.getModalId();

        String flowId = modalId.startsWith("user_") ? modalId.substring(5) : modalId;
        
        Flow flow = flowRegistry.getFlow(flowId);
        if (flow == null) {
            api.getPlatform().getLogger().warning("No flow found for modal: " + modalId + " (looking for flow: " + flowId + ")");
            event.reply("Modal processing failed").setEphemeral(true).queue();
            return;
        }

        FlowContext context = new FlowContext(event);

        String nextStep = "finish";
        if (flow.getStep(nextStep) != null) {
            flowExecutor.executeFromStep(flow, nextStep, context);
        } else {
            flowExecutor.executeFlow(flow, context);
        }
    }
}