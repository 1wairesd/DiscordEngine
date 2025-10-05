package com.wairesdindustries.discordengine.paper.tools;

import com.wairesdindustries.discordengine.api.DEAPI;
import com.wairesdindustries.discordengine.api.platform.DECommandSender;
import com.wairesdindustries.discordengine.api.platform.DEPlayer;
import com.wairesdindustries.discordengine.api.platform.Platform;
import com.wairesdindustries.discordengine.paper.api.platform.PaperCommandSender;
import com.wairesdindustries.discordengine.paper.api.platform.PaperPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PaperUtils {

    @NotNull
    public static DEPlayer fromBukkit(@NotNull Player player) {
        return new PaperPlayer(player);
    }

    @NotNull
    public static DECommandSender fromBukkit(@NotNull CommandSender sender) {
        return new PaperCommandSender(sender);
    }

    @NotNull
    public static Player toBukkit(@NotNull DEPlayer player) {
        return (Player) player.getHandler();
    }

    @NotNull
    public static CommandSender toBukkit(@NotNull DECommandSender sender) {
        return (CommandSender) sender.getHandler();
    }

    public static Plugin getDiscordEngine() {
        try {
            Platform platform = DEAPI.getInstance().getPlatform();

            Method method = platform.getClass().getDeclaredMethod("getPlugin");
            return (Plugin) method.invoke(platform);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
