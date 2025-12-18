package com.wairesdindustries.discordengine.api.discord.flow;

/**
 * Manager for Discord flow system
 */
public interface FlowManager {
    
    /**
     * Load all flows, modals, and entry points
     */
    void load();
    
    /**
     * Get the flow dispatcher for handling interactions
     */
    FlowDispatcher getFlowDispatcher();
    
    /**
     * Get the modal registry
     */
    ModalRegistry getModalRegistry();
    
    /**
     * Get the flow registry
     */
    FlowRegistry getFlowRegistry();
    
    /**
     * Get the entry point registry
     */
    EntryPointRegistry getEntryPointRegistry();
}