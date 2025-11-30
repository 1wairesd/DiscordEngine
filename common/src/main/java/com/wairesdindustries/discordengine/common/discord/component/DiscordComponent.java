package com.wairesdindustries.discordengine.common.discord.component;

import java.util.List;
import java.util.Map;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;

public class DiscordComponent implements com.wairesdindustries.discordengine.api.discord.component.DiscordComponent {
    private final String id;
    private final com.wairesdindustries.discordengine.api.discord.component.ComponentType type;
    private final Map<String, Object> properties;
    private final List<String> requiredPermissions;
    private final List<DiscordAction> onSubmitActions;
    private final List<DiscordAction> onClickActions;

    public DiscordComponent(String id, com.wairesdindustries.discordengine.api.discord.component.ComponentType type, 
                           Map<String, Object> properties, List<String> requiredPermissions, 
                           List<DiscordAction> onSubmitActions, List<DiscordAction> onClickActions) {
        this.id = id;
        this.type = type;
        this.properties = properties;
        this.requiredPermissions = requiredPermissions;
        this.onSubmitActions = onSubmitActions;
        this.onClickActions = onClickActions;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public com.wairesdindustries.discordengine.api.discord.component.ComponentType getType() {
        return type;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public List<String> getRequiredPermissions() {
        return requiredPermissions;
    }

    @Override
    public List<DiscordAction> getOnSubmitActions() {
        return onSubmitActions;
    }

    @Override
    public List<DiscordAction> getOnClickActions() {
        return onClickActions;
    }

    @Override
    public Object getProperty(String key) {
        return properties != null ? properties.get(key) : null;
    }

    @Override
    public String getPropertyAsString(String key) {
        Object value = getProperty(key);
        return value != null ? value.toString() : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getPropertyAsList(String key) {
        Object value = getProperty(key);
        return value instanceof List ? (List<Map<String, Object>>) value : null;
    }
}
