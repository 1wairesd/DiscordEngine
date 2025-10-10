package com.wairesdindustries.discordengine.common.scheduler;

import com.wairesdindustries.discordengine.api.addon.Addon;
import com.wairesdindustries.discordengine.api.scheduler.SchedulerTask;

import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

public class WrappedTask implements SchedulerTask {

    private final Addon owner;
    private final int taskId;
    private final boolean sync;
    private final boolean external;

    private final Runnable r;
    private final Consumer<SchedulerTask> c;

    private volatile boolean cancelled = false;
    private ScheduledFuture<?> future;

    @SuppressWarnings("unchecked")
    public WrappedTask(Addon owner, int taskId, boolean sync, Object task) {
        this(owner, taskId, sync, task, false);
    }

    @SuppressWarnings("unchecked")
    public WrappedTask(Addon owner, int taskId, boolean sync, Object task, boolean external) {
        this.owner = owner;
        this.taskId = taskId;
        this.sync = sync;
        this.external = external;

        if (task instanceof Runnable) {
            this.r = (Runnable) task;
            this.c = null;
        } else if (task instanceof Consumer) {
            this.r = null;
            this.c = (Consumer<SchedulerTask>) task;
        } else {
            throw new IllegalArgumentException("Invalid task type: " + task.getClass());
        }
    }

    @Override
    public void run() {
        if (cancelled) return;
        try {
            if (r != null) r.run();
            if (c != null) c.accept(this);
        } catch (Throwable t) {
            System.err.println("Exception while executing task: " + taskId);
            t.printStackTrace();
        }
    }

    @Override
    public int getTaskId() {
        return taskId;
    }

    @Override
    public boolean isSync() {
        return sync;
    }

    @Override
    public Addon getOwner() {
        return owner;
    }

    @Override
    public boolean isCancelled() {
        return cancelled || (future != null && future.isCancelled());
    }

    @Override
    public void cancel() {
        if (isCancelled()) return;

        cancelled = true;
        if (future != null) {
            future.cancel(false);
        }

        // В PaperScheduler будет проверка external и отмена внешней задачи
        // DCAPI.getInstance().getPlatform().getScheduler().cancel(taskId, external);
    }

    public void setFuture(ScheduledFuture<?> future) {
        this.future = future;
    }
}
