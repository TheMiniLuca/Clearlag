package com.github.theminiluca.clear.lag.api;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public interface NMS {
    BukkitTask startUntrackerTask(Plugin plugin);

    BukkitTask startUCheckTask(Plugin plugin);
}
