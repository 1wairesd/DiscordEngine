package com.wairesdindustries.discordengine.common.discord.flow;

import com.wairesdindustries.discordengine.api.discord.flow.EntryPointRegistry;

import java.util.HashMap;
import java.util.Map;

public class EntryPointRegistryImpl implements EntryPointRegistry {
    private final Map<String, String> commandToFlow = new HashMap<>();
    private final Map<String, String> buttonToFlow = new HashMap<>();

    public void registerCommand(String commandName, String flowId) {
        commandToFlow.put(commandName, flowId);
    }

    public void registerButton(String buttonId, String flowId) {
        buttonToFlow.put(buttonId, flowId);
    }

    public String getFlowForCommand(String commandName) {
        return commandToFlow.get(commandName);
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
        buttonToFlow.clear();
    }
}