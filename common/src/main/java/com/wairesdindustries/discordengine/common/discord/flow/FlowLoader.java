package com.wairesdindustries.discordengine.common.discord.flow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import com.wairesdindustries.discordengine.common.DiscordEngine;
import com.wairesdindustries.discordengine.common.discord.flow.actions.AddRoleAction;
import com.wairesdindustries.discordengine.common.discord.flow.actions.OpenModalAction;
import com.wairesdindustries.discordengine.common.discord.flow.actions.SendMessageAction;
import com.wairesdindustries.discordengine.common.discord.flow.modal.ModalDefinition;
import com.wairesdindustries.discordengine.common.discord.flow.modal.ModalRegistryImpl;

public class FlowLoader {
    private final DiscordEngine api;
    private final FlowRegistryImpl flowRegistry;
    private final ModalRegistryImpl modalRegistry;

    public FlowLoader(DiscordEngine api, FlowRegistryImpl flowRegistry, ModalRegistryImpl modalRegistry) {
        this.api = api;
        this.flowRegistry = flowRegistry;
        this.modalRegistry = modalRegistry;
    }

    public void load() {
        flowRegistry.clear();
        modalRegistry.clear();

        File dataFolder = api.getPlatform().getDataFolder();
        File botsFolder = new File(dataFolder, "bots");
        
        if (!botsFolder.exists() || !botsFolder.isDirectory()) {
            return;
        }

        File[] botDirs = botsFolder.listFiles(File::isDirectory);
        if (botDirs == null) return;

        for (File botDir : botDirs) {
            File discordFolder = new File(botDir, "discord");
            if (discordFolder.exists()) {
                loadBotFlows(discordFolder);
            }
        }
    }

    private void loadBotFlows(File discordFolder) {
        loadModals(new File(discordFolder, "modals"));

        loadFlows(new File(discordFolder, "flows"));
    }

    private void loadModals(File modalsFolder) {
        if (!modalsFolder.exists() || !modalsFolder.isDirectory()) {
            return;
        }

        File[] modalFiles = modalsFolder.listFiles((dir, name) -> 
            name.endsWith(".yml") || name.endsWith(".yaml"));
        
        if (modalFiles == null) return;

        for (File modalFile : modalFiles) {
            loadModalFile(modalFile);
        }
    }

    private void loadModalFile(File modalFile) {
        try {
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .file(modalFile)
                .build();
            
            ConfigurationNode root = loader.load();
            ConfigurationNode modalNode = root.node("modal");
            
            if (modalNode.virtual()) {
                api.getPlatform().getLogger().warning("No modal definition found in " + modalFile.getName());
                return;
            }

            String id = modalNode.node("id").getString();
            String title = modalNode.node("title").getString();
            
            if (id == null || title == null) {
                api.getPlatform().getLogger().warning("Modal missing id or title in " + modalFile.getName());
                return;
            }

            List<ModalDefinition.Input> inputs = new ArrayList<>();
            ConfigurationNode inputsNode = modalNode.node("inputs");
            
            if (!inputsNode.virtual() && inputsNode.isList()) {
                for (ConfigurationNode inputNode : inputsNode.childrenList()) {
                    String inputId = inputNode.node("id").getString();
                    String label = inputNode.node("label").getString();
                    String style = inputNode.node("style").getString("SHORT");
                    String placeholder = inputNode.node("placeholder").getString("");
                    boolean required = inputNode.node("required").getBoolean(true);
                    
                    if (inputId != null && label != null) {
                        inputs.add(new ModalDefinition.Input(inputId, label, style, placeholder, required));
                    }
                }
            }

            ModalDefinition.OnSubmit onSubmit = null;
            ConfigurationNode onSubmitNode = modalNode.node("on_submit");
            if (!onSubmitNode.virtual()) {
                String flowId = onSubmitNode.node("flow").getString();
                String stepId = onSubmitNode.node("step").getString();
                if (flowId != null && stepId != null) {
                    onSubmit = new ModalDefinition.OnSubmit(flowId, stepId);
                }
            }

            ModalDefinition modal = new ModalDefinition(id, title, inputs, onSubmit);
            modalRegistry.registerModal(modal);

            
        } catch (IOException e) {
            api.getPlatform().getLogger().severe("Failed to load modal from " + modalFile.getName() + ": " + e.getMessage());
        }
    }

    private void loadFlows(File flowsFolder) {
        if (!flowsFolder.exists() || !flowsFolder.isDirectory()) {
            return;
        }

        File[] flowFiles = flowsFolder.listFiles((dir, name) -> 
            name.endsWith(".yml") || name.endsWith(".yaml"));
        
        if (flowFiles == null) return;

        for (File flowFile : flowFiles) {
            loadFlowFile(flowFile);
        }
    }

    private void loadFlowFile(File flowFile) {
        try {
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .file(flowFile)
                .build();
            
            ConfigurationNode root = loader.load();
            ConfigurationNode flowNode = root.node("flow");
            
            if (flowNode.virtual()) {
                api.getPlatform().getLogger().warning("No flow definition found in " + flowFile.getName());
                return;
            }

            String id = flowNode.node("id").getString();
            if (id == null) {
                api.getPlatform().getLogger().warning("Flow missing id in " + flowFile.getName());
                return;
            }

            Map<String, FlowStep> steps = new HashMap<>();
            ConfigurationNode stepsNode = flowNode.node("steps");
            
            if (!stepsNode.virtual()) {
                for (Map.Entry<Object, ? extends ConfigurationNode> entry : stepsNode.childrenMap().entrySet()) {
                    String stepId = entry.getKey().toString();
                    ConfigurationNode stepNode = entry.getValue();
                    
                    FlowStep step = parseFlowStep(stepId, stepNode);
                    if (step != null) {
                        steps.put(stepId, step);
                    }
                }
            }

            if (!steps.isEmpty()) {
                String startStep = steps.containsKey("start") ? "start" : steps.keySet().iterator().next();
                
                Flow flow = new Flow(id, steps, startStep);
                flowRegistry.registerFlow(flow);
            }
            
        } catch (IOException e) {
            api.getPlatform().getLogger().severe("Failed to load flow from " + flowFile.getName() + ": " + e.getMessage());
        }
    }

    private FlowStep parseFlowStep(String stepId, ConfigurationNode stepNode) {
        List<FlowAction> actions = new ArrayList<>();
        String nextStep = stepNode.node("next").getString();

        ConfigurationNode actionNode = stepNode.node("action");
        if (!actionNode.virtual()) {
            FlowAction action = parseAction(actionNode);
            if (action != null) {
                actions.add(action);
            }
        }

        ConfigurationNode actionsNode = stepNode.node("actions");
        if (!actionsNode.virtual() && actionsNode.isList()) {
            for (ConfigurationNode singleActionNode : actionsNode.childrenList()) {
                FlowAction action = parseAction(singleActionNode);
                if (action != null) {
                    actions.add(action);
                }
            }
        }

        return new FlowStep(stepId, actions, nextStep);
    }

    private FlowAction parseAction(ConfigurationNode actionNode) {
        if (actionNode.virtual()) {
            return null;
        }

        if (actionNode.raw() instanceof String) {
            String actionType = (String) actionNode.raw();
            return createSimpleAction(actionType);
        }

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : actionNode.childrenMap().entrySet()) {
            String actionType = entry.getKey().toString();
            ConfigurationNode params = entry.getValue();
            
            return createAction(actionType, params);
        }

        return null;
    }

    private FlowAction createSimpleAction(String actionType) {
        switch (actionType) {
            case "open_modal":
                return null;
            default:
                return null;
        }
    }

    private FlowAction createAction(String actionType, ConfigurationNode params) {
        switch (actionType) {
            case "open_modal":
                String modalId = params.getString();

                if (modalId != null) {
                    return new OpenModalAction(modalId, modalRegistry);
                }
                break;
                
            case "send_message":
                String content = params.node("content").getString();
                String key = params.node("key").getString();
                if (content != null && !content.trim().isEmpty()) {
                    return new SendMessageAction(content);
                } else if (key != null && !key.trim().isEmpty()) {
                    return new SendMessageAction("", key);
                } else {
                    return new SendMessageAction("Action completed successfully!");
                }

            case "add_role":
                String roleId = params.node("role_id").getString();
                if (roleId != null) {
                    return new AddRoleAction(roleId);
                }
                break;
        }
        
        return null;
    }
}