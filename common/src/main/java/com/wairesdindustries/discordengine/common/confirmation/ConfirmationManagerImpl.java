package com.wairesdindustries.discordengine.common.confirmation;

import com.wairesdindustries.discordengine.api.DEAPI;
import com.wairesdindustries.discordengine.api.addon.Addon;
import com.wairesdindustries.discordengine.api.platform.DECommandSender;
import com.wairesdindustries.discordengine.api.platform.DEConfirmationManager;
import com.wairesdindustries.discordengine.api.platform.DEPlayer;
import com.wairesdindustries.discordengine.api.scheduler.SchedulerTask;
import com.wairesdindustries.discordengine.api.tools.DETools;
import com.wairesdindustries.discordengine.api.tools.Placeholder;
import com.wairesdindustries.discordengine.common.tools.LocalPlaceholder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ConfirmationManagerImpl implements DEConfirmationManager {

    private final DEAPI api;
    private final Map<UUID, Confirmation> confirmations = new ConcurrentHashMap<>();
    private Confirmation consoleConfirmation;

    public ConfirmationManagerImpl(DEAPI api) {
        this.api = api;
    }

    @Override
    public void requestConfirmation(@NotNull DECommandSender sender, @NotNull Runnable onConfirm, @NotNull Runnable onCancel) {
        String template = api.getConfigManager().getMessages()
                .getString("confirmation-invalid-input", "&cInvalid input. Please enter &aY &cor &cN");

        String invalidInputMsg = applyPlaceholders(template, new LocalPlaceholder("%ignored%", ""));

        requestConfirmation(sender, Map.of(
                "y", ignored -> onConfirm.run(),
                "n", ignored -> onCancel.run()
        ), invalidInputMsg);
    }


    @Override
    public void requestConfirmation(@NotNull DECommandSender sender, @NotNull Map<String, Consumer<DECommandSender>> actions, @NotNull String invalidInputMessage) {
        requestConfirmation(sender, actions, invalidInputMessage, 30);
    }

    @Override
    public void requestConfirmation(@NotNull DECommandSender sender, @NotNull Runnable onConfirm, @NotNull Runnable onCancel, long timeoutSeconds) {
        String template = api.getConfigManager().getMessages()
                .getString("confirmation-invalid-input", "&cInvalid input. Please enter &aY &cor &cN");

        String invalidInputMsg = applyPlaceholders(template, new LocalPlaceholder("%ignored%", ""));

        requestConfirmation(sender, Map.of(
                "y", ignored -> onConfirm.run(),
                "n", ignored -> onCancel.run()
        ), invalidInputMsg, timeoutSeconds);
    }



    @Override
    public void requestConfirmation(@NotNull DECommandSender sender, @NotNull Map<String, Consumer<DECommandSender>> actions,
                                    @NotNull String invalidInputMessage, long timeoutSeconds) {
        Addon addon = getAddon(sender);

        if (sender.isConsole()) {
            consoleConfirmation = new Confirmation(null, actions, invalidInputMessage);

            if (timeoutSeconds > 0) {
                String timeoutTemplate = api.getConfigManager().getMessages()
                        .getString("confirmation-timeout", "&cConfirmation timed out (%seconds% sec)");

                String timeoutMsg = applyPlaceholders(timeoutTemplate, new LocalPlaceholder("%seconds%", String.valueOf(timeoutSeconds)));

                SchedulerTask timeoutTask = api.getPlatform().getScheduler().run(addon, () -> {
                    if (consoleConfirmation != null) {
                        consoleConfirmation = null;
                        sendMessage(sender, timeoutMsg);
                    }
                }, timeoutSeconds * 20);
                consoleConfirmation.setTimeoutTask(timeoutTask);
            }
        } else {
            UUID uuid = getSenderUUID(sender);
            if (uuid != null) {
                Confirmation confirmation = new Confirmation(uuid, actions, invalidInputMessage);
                confirmations.put(uuid, confirmation);

                if (timeoutSeconds > 0) {
                    String timeoutTemplate = api.getConfigManager().getMessages()
                            .getString("confirmation-timeout", "&cConfirmation timed out (%seconds% sec)");

                    String timeoutMsg = applyPlaceholders(timeoutTemplate, new LocalPlaceholder("%seconds%", String.valueOf(timeoutSeconds)));

                    SchedulerTask timeoutTask = api.getPlatform().getScheduler().run(addon, () -> {
                        Confirmation conf = confirmations.remove(uuid);
                        if (conf != null) sendMessage(sender, timeoutMsg);
                    }, timeoutSeconds * 20);
                    confirmation.setTimeoutTask(timeoutTask);
                }
            }
        }
    }

    public boolean hasConfirmation(DECommandSender sender) {
        if (sender.isConsole()) return consoleConfirmation != null;
        UUID uuid = getSenderUUID(sender);
        return uuid != null && confirmations.containsKey(uuid);
    }

    public void processConfirmationInput(DECommandSender sender, String input) {
        if (sender.isConsole()) {
            if (consoleConfirmation == null) return;
            handleInput(sender, consoleConfirmation, input);
        } else {
            UUID uuid = getSenderUUID(sender);
            if (uuid != null) {
                Confirmation confirmation = confirmations.get(uuid);
                if (confirmation == null) return;
                handleInput(sender, confirmation, input);
            }
        }
    }

    public void cancelConfirmation(DECommandSender sender) {
        if (sender.isConsole()) {
            if (consoleConfirmation != null) {
                SchedulerTask timeoutTask = consoleConfirmation.getTimeoutTask();
                if (timeoutTask != null) timeoutTask.cancel();
                consoleConfirmation = null;
            }
        } else {
            UUID uuid = getSenderUUID(sender);
            if (uuid != null) {
                Confirmation confirmation = confirmations.remove(uuid);
                if (confirmation != null) {
                    SchedulerTask timeoutTask = confirmation.getTimeoutTask();
                    if (timeoutTask != null) timeoutTask.cancel();
                }
            }
        }
    }

    private void handleInput(DECommandSender sender, Confirmation confirmation, String input) {
        Consumer<DECommandSender> action = confirmation.getActions().get(input.toLowerCase());
        if (action != null) {
            SchedulerTask timeoutTask = confirmation.getTimeoutTask();
            if (timeoutTask != null) timeoutTask.cancel();

            action.accept(sender);

            if (sender.isConsole()) consoleConfirmation = null;
            else {
                UUID uuid = getSenderUUID(sender);
                if (uuid != null) confirmations.remove(uuid);
            }
        } else {
            sendMessage(sender, confirmation.getInvalidInputMessage());
        }
    }

    protected UUID getSenderUUID(DECommandSender sender) {
        if (sender instanceof DEPlayer player) return player.getUniqueId();
        return null;
    }

    protected Addon getAddon(DECommandSender sender) {
        return null;
    }

    protected void sendMessage(DECommandSender sender, String message) {
        if (sender == null) System.out.println(message);
        else sender.sendMessage(message);
    }

    private String applyPlaceholders(String template, Placeholder... placeholders) {
        return DETools.prefix(DETools.rt(template, placeholders));
    }
}
