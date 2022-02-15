package com.github.theminiluca.clear.lag.plugin.api;

import org.bukkit.plugin.Plugin;

public interface LatestNMS extends NMS{
    void startTasks(Plugin plugin, int tick);
}
