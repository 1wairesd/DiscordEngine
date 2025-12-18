package com.wairesdindustries.discordengine.api.discord.flow;

import java.util.Map;

/**
 * Registry for entry points (commands and buttons) that trigger flows
 */
public interface EntryPointRegistry {
    
    /**
     * Register a command that triggers a flow
     */
    void registerCommand(String commandId, String flowId);
    
    /**
     * Register a button that triggers a flow
     */
    void registerButton(String buttonId, String flowId);
    
    /**
     * Get the flow ID for a command
     */
    String getFlowForCommand(String commandId);
    
    /**
     * Get the flow ID for a button
     */
    String getFlowForButton(String buttonId);
    
    /**
     * Get all registered commands
     */
    Map<String, String> getAllCommands();
    
    /**
     * Get all registered buttons
     */
    Map<String, String> getAllButtons();
    
    /**
     * Clear all registered entry points
     */
    void clear();
}