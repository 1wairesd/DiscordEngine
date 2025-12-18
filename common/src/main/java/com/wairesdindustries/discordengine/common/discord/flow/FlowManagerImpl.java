package com.wairesdindustries.discordengine.common.discord.flow;

import com.wairesdindustries.discordengine.api.discord.flow.*;
import com.wairesdindustries.discordengine.common.DiscordEngine;
import com.wairesdindustries.discordengine.common.discord.flow.modal.ModalRegistryImpl;

public class FlowManagerImpl implements FlowManager {
    private final DiscordEngine api;
    private final FlowRegistryImpl flowRegistry;
    private final ModalRegistryImpl modalRegistry;
    private final EntryPointRegistryImpl entryPointRegistry;
    private final FlowExecutor flowExecutor;
    private final FlowDispatcherImpl flowDispatcher;
    private final FlowLoader flowLoader;
    private final EntryPointLoader entryPointLoader;

    public FlowManagerImpl(DiscordEngine api) {
        this.api = api;
        this.flowRegistry = new FlowRegistryImpl();
        this.modalRegistry = new ModalRegistryImpl();
        this.entryPointRegistry = new EntryPointRegistryImpl();
        this.flowExecutor = new FlowExecutor(api);
        this.flowDispatcher = new FlowDispatcherImpl(api, flowRegistry, entryPointRegistry, flowExecutor);
        this.flowLoader = new FlowLoader(api, flowRegistry, modalRegistry);
        this.entryPointLoader = new EntryPointLoader(api, entryPointRegistry);
    }

    public void load() {
        flowLoader.load();
        entryPointLoader.load();
    }

    @Override
    public FlowRegistry getFlowRegistry() {
        return flowRegistry;
    }

    @Override
    public ModalRegistry getModalRegistry() {
        return modalRegistry;
    }

    @Override
    public EntryPointRegistry getEntryPointRegistry() {
        return entryPointRegistry;
    }

    public FlowExecutor getFlowExecutor() {
        return flowExecutor;
    }

    @Override
    public FlowDispatcher getFlowDispatcher() {
        return flowDispatcher;
    }
}