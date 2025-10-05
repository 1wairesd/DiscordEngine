package com.wairesdindustries.discordengine.paper.api.platform;

import com.wairesdindustries.discordengine.api.platform.DECommandSender;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PaperCommandSender implements DECommandSender {

    private final CommandSender sender;

    public PaperCommandSender(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public @NotNull String getName() {
        return sender.getName();
    }

    @Override
    public @NotNull CommandSender getHandler() {
        return sender;
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public void sendMessage(@NotNull String text) {
        sender.sendMessage(text);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        PaperCommandSender that = (PaperCommandSender) object;
        return Objects.equals(sender, that.sender);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sender);
    }
}
