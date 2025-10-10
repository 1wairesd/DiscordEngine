package com.wairesdindustries.discordengine.paper;

import com.wairesdindustries.discordengine.api.addon.Addon;
import com.wairesdindustries.discordengine.api.scheduler.SchedulerTask;
import com.wairesdindustries.discordengine.common.scheduler.BackendScheduler;
import com.wairesdindustries.discordengine.common.scheduler.WrappedTask;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class PaperScheduler extends BackendScheduler {

    private final Plugin plugin;
    private final org.bukkit.scheduler.BukkitScheduler scheduler;

    public PaperScheduler(DEPaperBackend backend) {
        this.plugin = backend.getPlugin();
        this.scheduler = plugin.getServer().getScheduler();
    }

    private WrappedTask wrapper(Addon addon, BukkitTask task) {
        return new WrappedTask(addon, task.getTaskId(), task.isSync(), task, true);
    }

    @Override
    public SchedulerTask run(Addon addon, Runnable task) {
        BukkitTask bukkitTask = scheduler.runTask(plugin, task);
        return wrapper(addon, bukkitTask);
    }

    @Override
    public SchedulerTask run(Addon addon, Runnable task, long delayTicks) {
        BukkitTask bukkitTask = scheduler.runTaskLater(plugin, task, delayTicks);
        return wrapper(addon, bukkitTask);
    }

    @Override
    public SchedulerTask run(Addon addon, Runnable task, long delayTicks, long periodTicks) {
        BukkitTask bukkitTask = scheduler.runTaskTimer(plugin, task, delayTicks, periodTicks);
        return wrapper(addon, bukkitTask);
    }

    @Override
    public void run(Addon addon, Consumer<SchedulerTask> task) {
        scheduler.runTask(plugin, (bukkitTask) -> task.accept(wrapper(addon, bukkitTask)));
    }

    @Override
    public void run(Addon addon, Consumer<SchedulerTask> task, long delayTicks) {
        scheduler.runTaskLater(plugin, (bukkitTask) -> task.accept(wrapper(addon, bukkitTask)), delayTicks);
    }

    @Override
    public void run(Addon addon, Consumer<SchedulerTask> task, long delayTicks, long periodTicks) {
        scheduler.runTaskTimer(plugin, (bukkitTask) -> task.accept(wrapper(addon, bukkitTask)), delayTicks, periodTicks);
    }

    @Override
    public SchedulerTask async(Addon addon, Runnable task, long delayTicks) {
        BukkitTask bukkitTask = scheduler.runTaskLaterAsynchronously(plugin, task, delayTicks);
        return wrapper(addon, bukkitTask);
    }

    @Override
    public SchedulerTask async(Addon addon, Runnable task, long delayTicks, long periodTicks) {
        BukkitTask bukkitTask = scheduler.runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks);
        return wrapper(addon, bukkitTask);
    }

    @Override
    public void async(Addon addon, Consumer<SchedulerTask> task, long delayTicks) {
        scheduler.runTaskLaterAsynchronously(plugin, (bukkitTask) -> task.accept(wrapper(addon, bukkitTask)), delayTicks);
    }

    @Override
    public void async(Addon addon, Consumer<SchedulerTask> task, long delayTicks, long periodTicks) {
        scheduler.runTaskTimerAsynchronously(plugin, (bukkitTask) -> task.accept(wrapper(addon, bukkitTask)), delayTicks, periodTicks);
    }

    @Override
    public void cancel(int taskId, boolean external) {
        super.cancel(taskId, external);
        if (external) scheduler.cancelTask(taskId);
    }
}
