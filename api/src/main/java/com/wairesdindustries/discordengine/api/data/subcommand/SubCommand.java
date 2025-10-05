package com.wairesdindustries.discordengine.api.data.subcommand;

import com.wairesdindustries.discordengine.api.addon.Addon;
import com.wairesdindustries.discordengine.api.platform.DECommandSender;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a subcommand with execution and tab-completion capabilities.
 *
 */
@Accessors(fluent = true)
@Builder
@Getter
public final class SubCommand implements SubCommandExecutor, SubCommandTabCompleter {

    @NotNull private Addon addon;
    @NotNull private String name;
    @Nullable private SubCommandExecutor executor;
    @Nullable private SubCommandTabCompleter tabCompleter;
    @Nullable private String description;
    @Nullable private String permission;
    @Nullable private String[] args;


    @Override
    public boolean execute(@NotNull DECommandSender sender, @NotNull String label, @NotNull String[] args) throws SubCommandException {
        if(executor == null) return false;
        return executor.execute(sender, label, args);
    }

    @Override
    public List<String> getTabCompletions(@NotNull DECommandSender sender, @NotNull String label, @NotNull String[] args) throws SubCommandException {
        if(tabCompleter == null) return new ArrayList<>();
        return tabCompleter.getTabCompletions(sender, label, args);
    }

}


