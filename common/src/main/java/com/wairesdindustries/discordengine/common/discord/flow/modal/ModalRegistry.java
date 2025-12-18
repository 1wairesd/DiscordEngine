package com.wairesdindustries.discordengine.common.discord.flow.modal;

import java.util.HashMap;
import java.util.Map;

public class ModalRegistry {
    private final Map<String, ModalDefinition> modals = new HashMap<>();

    public void registerModal(ModalDefinition modal) {
        modals.put(modal.getId(), modal);
    }

    public ModalDefinition getModal(String id) {
        return modals.get(id);
    }

    public Map<String, ModalDefinition> getAllModals() {
        return new HashMap<>(modals);
    }

    public void clear() {
        modals.clear();
    }
}