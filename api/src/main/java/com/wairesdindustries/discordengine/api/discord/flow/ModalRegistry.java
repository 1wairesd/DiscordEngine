package com.wairesdindustries.discordengine.api.discord.flow;

import java.util.Map;

/**
 * Registry for Discord modals
 */
public interface ModalRegistry {
    
    /**
     * Register a modal definition
     */
    void registerModal(Object modalDefinition);
    
    /**
     * Get a modal by ID
     */
    Object getModal(String modalId);
    
    /**
     * Get all registered modals
     */
    Map<String, Object> getAllModals();
    
    /**
     * Clear all registered modals
     */
    void clear();
}