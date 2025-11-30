package com.wairesdindustries.discordengine.api.discord.component;

public class ComponentReference {
    private final String filePath;
    private final String componentId;

    public ComponentReference(String reference) {
        if (reference == null || !reference.contains(":")) {
            throw new IllegalArgumentException("Invalid component reference format. Expected: 'file.yml:component-id'");
        }
        
        String[] parts = reference.split(":", 2);
        this.filePath = parts[0];
        this.componentId = parts[1];
    }

    public ComponentReference(String filePath, String componentId) {
        this.filePath = filePath;
        this.componentId = componentId;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getComponentId() {
        return componentId;
    }

    public String toReferenceString() {
        return filePath + ":" + componentId;
    }

    @Override
    public String toString() {
        return toReferenceString();
    }
}
