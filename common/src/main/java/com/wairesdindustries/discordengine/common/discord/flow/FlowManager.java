package com.wairesdindustries.discordengine.common.flow;

import com.wairesdindustries.discordengine.common.DiscordEngine;
import com.wairesdindustries.discordengine.common.flow.modal.ModalRegistry;

public class FlowManager {
    private final DiscordEngine api;
    private final FlowRegistry flowRegistry;
    private final ModalRegistry modalRegistry;
    private final EntryPointRegistry entryPointRegistry;
    private final FlowExecutor flowExecutor;
    private final FlowDispatcher flowDispatcher;
    private final FlowLoader flowLoader;
    private final EntryPointLoader entryPointLoader;

    public FlowManager(DiscordEngine api) {
        this.api = api;
        this.flowRegistry = new FlowRegistry();
        this.modalRegistry = new ModalRegistry();
        this.entryPointRegistry = new EntryPointRegistry();
        this.flowExecutor = new FlowExecutor(api);
        this.flowDispatcher = new FlowDispatcher(api, flowRegistry, entryPointRegistry, flowExecutor);
        this.flowLoader = new FlowLoader(api, flowRegistry, modalRegistry);
        this.entryPointLoader = new EntryPointLoader(api, entryPointRegistry);
    }

    public void load() {
        flowLoader.load();
        entryPointLoader.load();
    }

    public FlowRegistry getFlowRegistry() {
        return flowRegistry;
    }

    public ModalRegistry getModalRegistry() {
        return modalRegistry;
    }

    public EntryPointRegistry getEntryPointRegistry() {
        return entryPointRegistry;
    }

    public FlowExecutor getFlowExecutor() {
        return flowExecutor;
    }

    public FlowDispatcher getFlowDispatcher() {
        return flowDispatcher;
    }
}