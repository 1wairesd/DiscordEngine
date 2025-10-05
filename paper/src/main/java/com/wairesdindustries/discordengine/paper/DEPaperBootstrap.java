package com.wairesdindustries.discordengine.paper;

import org.bukkit.plugin.java.JavaPlugin;

public final class DEPaperBootstrap extends JavaPlugin {
    private DEPaperBackend backend;

    @Override
    public void onLoad() {
        backend = new DEPaperBackend(this);
    }

    @Override
    public void onEnable() {
        backend.load();
    }

    @Override
    public void onDisable() {
        backend.unload();
    }
}
