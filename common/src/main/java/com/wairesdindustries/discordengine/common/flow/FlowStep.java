package com.wairesdindustries.discordengine.common.flow;

import java.util.List;

public class FlowStep {
    private final String id;
    private final List<FlowAction> actions;
    private final String nextStep;

    public FlowStep(String id, List<FlowAction> actions, String nextStep) {
        this.id = id;
        this.actions = actions;
        this.nextStep = nextStep;
    }

    public String getId() {
        return id;
    }

    public List<FlowAction> getActions() {
        return actions;
    }

    public String getNextStep() {
        return nextStep;
    }
}