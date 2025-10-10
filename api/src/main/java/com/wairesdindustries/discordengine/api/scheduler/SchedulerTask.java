package com.wairesdindustries.discordengine.api.scheduler;

import com.wairesdindustries.discordengine.api.addon.Addon;

public interface SchedulerTask extends Runnable {

    int getTaskId();

    boolean isSync();

    boolean isCancelled();

    Addon getOwner();

    void cancel();

}