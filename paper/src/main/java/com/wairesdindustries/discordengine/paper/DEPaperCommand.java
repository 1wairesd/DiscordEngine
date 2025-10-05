package com.wairesdindustries.discordengine.paper;

import com.wairesdindustries.discordengine.api.platform.DECommandSender;
import com.wairesdindustries.discordengine.common.command.GlobalCommand;
import com.wairesdindustries.discordengine.paper.tools.PaperUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DEPaperCommand implements CommandExecutor, TabCompleter {

    private final GlobalCommand command;

    public DEPaperCommand(DEPaperBackend backend) {
        this.command = new GlobalCommand(backend);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        return command.execute(get(sender), s, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        return command.getTabCompletions(get(sender), s, args);
    }

    private static DECommandSender get(CommandSender sender) {
        return sender instanceof Player ? PaperUtils.fromBukkit((Player) sender) : PaperUtils.fromBukkit(sender);
    }
}
