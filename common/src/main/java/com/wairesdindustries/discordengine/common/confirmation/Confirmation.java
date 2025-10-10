package com.wairesdindustries.discordengine.common.confirmation;

import com.wairesdindustries.discordengine.api.platform.DECommandSender;
import com.wairesdindustries.discordengine.api.scheduler.SchedulerTask;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class Confirmation {

    private final UUID owner;
    private final Map<String, Consumer<DECommandSender>> actions;
    private final String invalidInputMessage;
    private SchedulerTask timeoutTask;

    public Confirmation(@Nullable UUID owner,
                        Map<String, Consumer<DECommandSender>> actions,
                        String invalidInputMessage) {
        this.owner = owner;
        this.actions = actions;
        this.invalidInputMessage = invalidInputMessage;
    }

    public UUID getOwner() { return owner; }
    public Map<String, Consumer<DECommandSender>> getActions() { return actions; }
    public String getInvalidInputMessage() { return invalidInputMessage; }
    
    public SchedulerTask getTimeoutTask() { return timeoutTask; }
    public void setTimeoutTask(SchedulerTask timeoutTask) { this.timeoutTask = timeoutTask; }
}
