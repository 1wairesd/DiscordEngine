package com.wairesdindustries.discordengine.common.discord.flow;

import java.util.logging.Level;

import com.wairesdindustries.discordengine.common.DiscordEngine;

public class FlowExecutor {
    private final DiscordEngine api;

    public FlowExecutor(DiscordEngine api) {
        this.api = api;
    }

    public void executeFlow(Flow flow, FlowContext context) {
        String currentStepId = flow.getStartStep();
        executeFromStep(flow, currentStepId, context);
    }

    public void executeFromStep(Flow flow, String stepId, FlowContext context) {
        try {
            FlowStep currentStep = flow.getStep(stepId);
            if (currentStep == null) {
                api.getPlatform().getLogger().warning("Flow step not found: " + stepId + " in flow " + flow.getId());
                return;
            }

            for (FlowAction action : currentStep.getActions()) {
                try {
                    action.execute(context);
                } catch (Exception e) {
                    api.getPlatform().getLogger().log(Level.SEVERE, 
                        "Error executing action in flow " + flow.getId() + ", step " + stepId, e);
                    return;
                }
            }

            String nextStepId = currentStep.getNextStep();
            if (nextStepId != null && !nextStepId.trim().isEmpty()) {
                executeFromStep(flow, nextStepId, context);
            }

        } catch (Exception e) {
            api.getPlatform().getLogger().log(Level.SEVERE, 
                "Error executing flow " + flow.getId() + " at step " + stepId, e);
        }
    }
}