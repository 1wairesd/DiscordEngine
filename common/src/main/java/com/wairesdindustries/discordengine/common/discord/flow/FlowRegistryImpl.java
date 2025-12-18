package com.wairesdindustries.discordengine.common.discord.flow;

import java.util.HashMap;
import java.util.Map;

import com.wairesdindustries.discordengine.api.discord.flow.FlowRegistry;

public class FlowRegistryImpl implements FlowRegistry {
    private final Map<String, Flow> flows = new HashMap<>();

    public void registerFlow(Flow flow) {
        flows.put(flow.getId(), flow);
    }

    @Override
    public void registerFlow(Object flow) {
        if (flow instanceof Flow) {
            registerFlow((Flow) flow);
        }
    }

    public Flow getFlowTyped(String id) {
        return flows.get(id);
    }

    @Override
    public Object getFlow(String flowId) {
        return flows.get(flowId);
    }

    public Map<String, Flow> getAllFlowsTyped() {
        return new HashMap<>(flows);
    }

    @Override
    public Map<String, Object> getAllFlows() {
        return new HashMap<>(flows);
    }

    @Override
    public void clear() {
        flows.clear();
    }
}