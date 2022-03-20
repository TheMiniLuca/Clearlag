package com.github.theminiluca.clear.lag.nms.v1_18_R2.tasks.villager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class MainTask implements Runnable {
    private final NormalActivityTask activityTask;
    private final RemoveActivityTask removeTask;
    private final Plugin plugin;

    public MainTask(Plugin plugin) {
        this.plugin = plugin;
        this.activityTask = new NormalActivityTask();
        this.removeTask = new RemoveActivityTask();

        run();
    }

    @Override
    public void run() {
        this.activityTask.run();
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this.removeTask, 1);
    }
}
