package com.wairesdindustries.discordengine.api.scheduler;

import com.wairesdindustries.discordengine.api.addon.Addon;

import java.util.function.Consumer;

public interface Scheduler {

    SchedulerTask run(Addon addon, Runnable task);

    SchedulerTask run(Addon addon, Runnable task, long delayTicks);

    SchedulerTask run(Addon addon, Runnable task, long delayTicks, long periodTicks);

    void run(Addon addon, Consumer<SchedulerTask> task);

    void run(Addon addon, Consumer<SchedulerTask> task, long delayTicks);

    void run(Addon addon, Consumer<SchedulerTask> task, long delayTicks, long periodTicks);

    SchedulerTask async(Addon addon, Runnable task, long delayTicks);

    SchedulerTask async(Addon addon, Runnable task, long delayTicks, long periodTicks);

    void async(Addon addon, Consumer<SchedulerTask> task, long delayTicks);

    void async(Addon addon, Consumer<SchedulerTask> task, long delayTicks, long periodTicks);

    void cancel(int taskId, boolean external);

    void shutdown();

}