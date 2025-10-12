package com.wairesdindustries.discordengine.api.tools;

import com.wairesdindustries.discordengine.api.DEAPI;
import com.wairesdindustries.discordengine.api.data.subcommand.SubCommand;
import com.wairesdindustries.discordengine.api.platform.DECommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Arrays;

public abstract class DETools {

    public static String[] parseRGB(String string) {
        if (string == null) return new String[0];
        return string.replaceAll(" ", "").split(",");
    }

    public static @NotNull String rc(String text) {
        return text == null ? "" : text.replace('&', '§');
    }

    public static String prefix(String text) {
        return rc(DEAPI.getInstance().getConfigManager().getMessages().getString("prefix") + text);
    }

    /**
     * Проверка команд в addonsMap для sender
     */
    public static boolean isHasCommandForSender(DECommandSender sender,
                                                Map<String, List<Map<String, SubCommand>>> addonsMap) {
        for (List<Map<String, SubCommand>> commandsList : addonsMap.values()) {
            if (isHasCommandForSender(sender, commandsList)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Перегрузка для списка карт команд
     */
    public static boolean isHasCommandForSender(DECommandSender sender,
                                                List<Map<String, SubCommand>> commands) {
        return commands.stream()
                .flatMap(map -> map.values().stream())
                .map(SubCommand::permission)
                .anyMatch(permission -> permission == null || sender.hasPermission(permission));
    }

    public static String rt(String text, Placeholder... placeholders) {
        if (text == null || placeholders.length == 0)
            return text;
        return rt(text, Arrays.asList(placeholders));
    }

    public static String rt(String text, List<Placeholder> placeholders) {
        if (text == null || placeholders == null || placeholders.isEmpty())
            return text;
        String result = text;
        for (Placeholder ph : placeholders) {
            result = result.replace(ph.name(), ph.value());
        }
        return rc(result);
    }

    public static @NotNull String formatConfirmationMessage(@NotNull String text, Object... args) {
        String formatted = String.format(text, args);
        return rc(formatted);
    }

    public static void sendConfirmationMessage(DECommandSender sender, String message) {
        if (sender == null) {
            System.out.println(message);
        } else {
            sender.sendMessage(message);
        }
    }
}
