package com.wairesdindustries.discordengine.common.discord.flow;

public class CommandDefinition {
    private final String id;
    private final String description;
    private final String flowId;
    private final CommandScope scope;

    public CommandDefinition(String id, String description, String flowId, CommandScope scope) {
        this.id = id;
        this.description = description;
        this.flowId = flowId;
        this.scope = scope != null ? scope : CommandScope.BOTH;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getFlowId() {
        return flowId;
    }

    public CommandScope getScope() {
        return scope;
    }

    public enum CommandScope {
        GUILD,    // Только на серверах
        DM,       // Только в ЛС
        BOTH      // И там, и там
    }
}