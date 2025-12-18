package com.wairesdindustries.discordengine.common.discord.flow;

import java.util.HashMap;
import java.util.Map;

public class FlowRegistry {
    private final Map<String, Flow> flows = new HashMap<>();

    public void registerFlow(Flow flow) {
        flows.put(flow.getId(), flow);
    }

    public Flow getFlow(String id) {
        return flows.get(id);
    }

    public Map<String, Flow> getAllFlows() {
        return new HashMap<>(flows);
    }

    public void clear() {
        flows.clear();
    }
}