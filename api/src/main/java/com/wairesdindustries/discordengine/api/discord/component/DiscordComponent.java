package com.wairesdindustries.discordengine.api.discord.component;

import java.util.List;
import java.util.Map;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;

public interface DiscordComponent {
    String getId();
    
    ComponentType getType();
    
    Map<String, Object> getProperties();
    
    List<String> getRequiredPermissions();
    
    List<DiscordAction> getOnSubmitActions();
    
    List<DiscordAction> getOnClickActions();
    
    Object getProperty(String key);
    
    String getPropertyAsString(String key);
    
    List<Map<String, Object>> getPropertyAsList(String key);
}
