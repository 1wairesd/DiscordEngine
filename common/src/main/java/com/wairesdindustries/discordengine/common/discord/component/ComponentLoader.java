package com.wairesdindustries.discordengine.common.discord.component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import com.wairesdindustries.discordengine.api.discord.component.ComponentReference;
import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.common.DiscordEngine;

public class ComponentLoader implements com.wairesdindustries.discordengine.api.discord.component.ComponentLoader {
    private final DiscordEngine api;
    private final Map<String, com.wairesdindustries.discordengine.api.discord.component.DiscordComponent> components = new HashMap<>();
    private final List<String> searchPaths = new ArrayList<>();

    public ComponentLoader(DiscordEngine api) {
        this.api = api;
        initializeSearchPaths();
    }

    private void initializeSearchPaths() {
        File dataFolder = api.getPlatform().getDataFolder();
        
        
        File botsFolder = new File(dataFolder, "bots");
        if (botsFolder.exists() && botsFolder.isDirectory()) {
            File[] botDirs = botsFolder.listFiles(File::isDirectory);
            if (botDirs != null) {
                for (File botDir : botDirs) {
                    File botDiscordFolder = new File(botDir, "discord/bot");
                    if (botDiscordFolder.exists()) {
                        searchPaths.add(botDiscordFolder.getAbsolutePath());
                    }
                }
            }
        }
    }

    @Override
    public void load() {
        components.clear();
        
        for (String searchPath : searchPaths) {
            loadComponentsFromDirectory(new File(searchPath), "");
        }
        
        api.getPlatform().getLogger().info("Loaded " + components.size() + " Discord components");
    }

    private void loadComponentsFromDirectory(File directory, String relativePath) {
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                String newRelativePath = relativePath.isEmpty() 
                    ? file.getName() 
                    : relativePath + "/" + file.getName();
                loadComponentsFromDirectory(file, newRelativePath);
            } else if (file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")) {
                if (isComponentDirectory(relativePath)) {
                    String filePath = relativePath.isEmpty() 
                        ? file.getName() 
                        : relativePath + "/" + file.getName();
                    loadComponentsFromFile(file, filePath);
                }
            }
        }
    }

    private boolean isComponentDirectory(String relativePath) {
        if (relativePath.isEmpty()) {
            return false;
        }

        String normalizedPath = relativePath.replace("\\", "/");
        return normalizedPath.startsWith("button/") || 
               normalizedPath.startsWith("modal/") ||
               normalizedPath.equals("button") ||
               normalizedPath.equals("modal");
    }

    private void loadComponentsFromFile(File file, String filePath) {
        try {
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .file(file)
                .build();
            
            ConfigurationNode root = loader.load();
            
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : root.childrenMap().entrySet()) {
                String key = entry.getKey().toString();
                if ("config".equals(key)) {
                    continue;
                }
                
                String componentId = key;
                ConfigurationNode componentNode = entry.getValue();
                
                DiscordComponent component = parseComponent(componentId, componentNode, filePath);
                if (component != null) {
                    String fullId = filePath + ":" + componentId;
                    components.put(fullId, component);
                }
            }
        } catch (IOException e) {
            api.getPlatform().getLogger().severe("Failed to load components from " + filePath + ": " + e.getMessage());
        }
    }

    private DiscordComponent parseComponent(String id, ConfigurationNode node, String filePath) {
        String typeStr = node.node("type").getString();
        if (typeStr == null || typeStr.trim().isEmpty()) {
            api.getPlatform().getLogger().warning("Component '" + id + "' in " + filePath + " has no type defined");
            return null;
        }

        com.wairesdindustries.discordengine.api.discord.component.ComponentType type;
        try {
            type = com.wairesdindustries.discordengine.api.discord.component.ComponentType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            api.getPlatform().getLogger().warning("Unknown component type: " + typeStr + " in " + filePath);
            return null;
        }

        Map<String, Object> properties = new HashMap<>();
        ConfigurationNode propsNode = node.node("properties");
        if (!propsNode.virtual()) {
            properties = convertNodeToMap(propsNode);
        }

        List<String> permissions = new ArrayList<>();
        ConfigurationNode permsNode = node.node("permissions");
        if (!permsNode.virtual() && permsNode.isList()) {
            permissions = permsNode.childrenList().stream()
                .map(n -> n.getString(""))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        }

        List<DiscordAction> onSubmitActions = parseActions(node.node("on_submit"));
        List<DiscordAction> onClickActions = parseActions(node.node("on_click"));

        return new DiscordComponent(id, type, properties, permissions, onSubmitActions, onClickActions);
    }

    private List<DiscordAction> parseActions(ConfigurationNode actionsNode) {
        if (actionsNode.virtual() || !actionsNode.isList()) {
            return new ArrayList<>();
        }

        List<DiscordAction> actions = new ArrayList<>();
        for (ConfigurationNode actionNode : actionsNode.childrenList()) {
            DiscordAction action = api.getDiscordCommandLoader().parseAction(actionNode);
            if (action != null) {
                actions.add(action);
            }
        }
        return actions;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> convertNodeToMap(ConfigurationNode node) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()) {
            String key = entry.getKey().toString();
            ConfigurationNode valueNode = entry.getValue();
            
            if (valueNode.isList()) {
                map.put(key, valueNode.childrenList().stream()
                    .map(this::convertNodeToValue)
                    .collect(Collectors.toList()));
            } else if (valueNode.isMap()) {
                map.put(key, convertNodeToMap(valueNode));
            } else {
                map.put(key, valueNode.raw());
            }
        }
        return map;
    }

    private Object convertNodeToValue(ConfigurationNode node) {
        if (node.isMap()) {
            return convertNodeToMap(node);
        } else if (node.isList()) {
            return node.childrenList().stream()
                .map(this::convertNodeToValue)
                .collect(Collectors.toList());
        } else {
            return node.raw();
        }
    }

    @Override
    public com.wairesdindustries.discordengine.api.discord.component.DiscordComponent getComponent(String reference) {
        return components.get(reference);
    }

    @Override
    public com.wairesdindustries.discordengine.api.discord.component.DiscordComponent getComponent(ComponentReference reference) {
        return getComponent(reference.toReferenceString());
    }

    @Override
    public Map<String, com.wairesdindustries.discordengine.api.discord.component.DiscordComponent> getAllComponents() {
        return new HashMap<>(components);
    }
}
