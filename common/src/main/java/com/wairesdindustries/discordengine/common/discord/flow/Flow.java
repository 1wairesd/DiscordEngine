package com.wairesdindustries.discordengine.common.discord.flow;

import java.util.Map;

public class Flow {
    private final String id;
    private final Map<String, FlowStep> steps;
    private final String startStep;

    public Flow(String id, Map<String, FlowStep> steps, String startStep) {
        this.id = id;
        this.steps = steps;
        this.startStep = startStep;
    }

    public String getId() {
        return id;
    }

    public Map<String, FlowStep> getSteps() {
        return steps;
    }

    public String getStartStep() {
        return startStep;
    }

    public FlowStep getStep(String stepId) {
        return steps.get(stepId);
    }
}