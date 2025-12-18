package com.wairesdindustries.discordengine.api.discord.flow;

/**
 * Dispatcher for handling Discord interactions and routing them to flows
 */
public interface FlowDispatcher {
    
    /**
     * Handle slash command interaction
     */
    void handleSlashCommand(Object event);
    
    /**
     * Handle button interaction
     */
    void handleButtonInteraction(Object event);
    
    /**
     * Handle modal interaction
     */
    void handleModalInteraction(Object event);
}