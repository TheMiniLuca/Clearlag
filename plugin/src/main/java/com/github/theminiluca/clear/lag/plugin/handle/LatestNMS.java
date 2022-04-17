package com.github.theminiluca.clear.lag.plugin.handle;

import org.bukkit.plugin.Plugin;

public interface LatestNMS extends NMS{
    void startTasks(Plugin plugin, int tick);
}
