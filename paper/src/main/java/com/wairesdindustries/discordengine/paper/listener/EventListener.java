package com.wairesdindustries.discordengine.paper.listener;

import com.wairesdindustries.discordengine.api.platform.DECommandSender;
import com.wairesdindustries.discordengine.paper.DEPaperBackend;
import com.wairesdindustries.discordengine.paper.api.platform.PaperCommandSender;
import com.wairesdindustries.discordengine.paper.tools.PaperUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class EventListener implements Listener {

    private final DEPaperBackend backend;

    public EventListener(DEPaperBackend backend) {
        this.backend = backend;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        DECommandSender sender = PaperUtils.fromBukkit(event.getPlayer());
        if (backend.getAPI().getConfirmationManager().hasConfirmation(sender)) {
            backend.getAPI().getConfirmationManager().processConfirmationInput(sender, event.getMessage());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onConsoleCommand(ServerCommandEvent event) {
        DECommandSender sender = new PaperCommandSender(Bukkit.getConsoleSender());
        if (backend.getAPI().getConfirmationManager().hasConfirmation(sender)) {
            backend.getAPI().getConfirmationManager().processConfirmationInput(sender, event.getCommand());
            event.setCancelled(true);
        }
    }
}
