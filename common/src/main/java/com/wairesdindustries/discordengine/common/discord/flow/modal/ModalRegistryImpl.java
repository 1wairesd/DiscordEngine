package com.wairesdindustries.discordengine.common.discord.flow.modal;

import java.util.HashMap;
import java.util.Map;

import com.wairesdindustries.discordengine.api.discord.flow.ModalRegistry;

public class ModalRegistryImpl implements ModalRegistry {
    private final Map<String, ModalDefinition> modals = new HashMap<>();

    public void registerModal(ModalDefinition modal) {
        modals.put(modal.getId(), modal);
    }

    @Override
    public void registerModal(Object modalDefinition) {
        if (modalDefinition instanceof ModalDefinition) {
            registerModal((ModalDefinition) modalDefinition);
        }
    }

    public ModalDefinition getModalTyped(String id) {
        return modals.get(id);
    }

    @Override
    public Object getModal(String modalId) {
        return modals.get(modalId);
    }

    public Map<String, ModalDefinition> getAllModalsTyped() {
        return new HashMap<>(modals);
    }

    @Override
    public Map<String, Object> getAllModals() {
        return new HashMap<>(modals);
    }

    @Override
    public void clear() {
        modals.clear();
    }
}