package com.wairesdindustries.discordengine.common.discord.flow;

import java.util.HashMap;
import java.util.Map;

import com.wairesdindustries.discordengine.api.discord.flow.EntryPointRegistry;

public class EntryPointRegistryImpl implements EntryPointRegistry {
    private final Map<String, String> commandToFlow = new HashMap<>();
    private final Map<String, CommandDefinition> commandDefinitions = new HashMap<>();
    private final Map<String, String> buttonToFlow = new HashMap<>();

    public void registerCommand(String commandName, String flowId) {
        commandToFlow.put(commandName, flowId);
    }

    public void registerCommand(CommandDefinition commandDef) {
        commandToFlow.put(commandDef.getId(), commandDef.getFlowId());
        commandDefinitions.put(commandDef.getId(), commandDef);
    }

    public void registerButton(String buttonId, String flowId) {
        buttonToFlow.put(buttonId, flowId);
    }

    public String getFlowForCommand(String commandName) {
        return commandToFlow.get(commandName);
    }

    public CommandDefinition getCommandDefinition(String commandName) {
        return commandDefinitions.get(commandName);
    }

    public String getFlowForButton(String buttonId) {
        return buttonToFlow.get(buttonId);
    }

    public Map<String, String> getAllCommands() {
        return new HashMap<>(commandToFlow);
    }

    public Map<String, String> getAllButtons() {
        return new HashMap<>(buttonToFlow);
    }

    public void clear() {
        commandToFlow.clear();
        commandDefinitions.clear();
        buttonToFlow.clear();
    }
}