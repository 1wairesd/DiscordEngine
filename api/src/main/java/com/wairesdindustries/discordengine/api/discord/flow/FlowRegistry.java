package com.wairesdindustries.discordengine.api.discord.flow;

import java.util.Map;

/**
 * Registry for Discord flows
 */
public interface FlowRegistry {
    
    /**
     * Register a flow
     */
    void registerFlow(Object flow);
    
    /**
     * Get a flow by ID
     */
    Object getFlow(String flowId);
    
    /**
     * Get all registered flows
     */
    Map<String, Object> getAllFlows();
    
    /**
     * Clear all registered flows
     */
    void clear();
}