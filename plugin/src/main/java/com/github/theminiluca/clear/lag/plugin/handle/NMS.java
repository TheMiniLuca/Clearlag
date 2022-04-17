package com.github.theminiluca.clear.lag.plugin.handle;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public interface NMS {
    BukkitTask startUntrackerTask(Plugin plugin, int tick);

    BukkitTask startUCheckTask(Plugin plugin, int tick);

    List<String> getEntities(boolean isname);
}
