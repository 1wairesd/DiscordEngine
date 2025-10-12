package com.wairesdindustries.discordengine.api.platform;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public interface DEConfirmationManager {

    void requestConfirmation(@NotNull DECommandSender sender,
                             @NotNull Runnable onConfirm,
                             @NotNull Runnable onCancel);

    void requestConfirmation(@NotNull DECommandSender sender,
                             @NotNull Map<String, Consumer<DECommandSender>> actions,
                             @NotNull String invalidInputMessage);

    void requestConfirmation(@NotNull DECommandSender sender,
                             @NotNull Runnable onConfirm,
                             @NotNull Runnable onCancel,
                             long timeoutSeconds);

    void requestConfirmation(@NotNull DECommandSender sender,
                             @NotNull Map<String, Consumer<DECommandSender>> actions,
                             @NotNull String invalidInputMessage,
                             long timeoutSeconds);

    boolean hasConfirmation(DECommandSender sender);

    void processConfirmationInput(DECommandSender sender, String input);

    void cancelConfirmation(DECommandSender sender);

}