package com.github.theminiluca.clear.lag.plugin.api;

import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import javax.swing.text.html.parser.Entity;
import java.util.List;

public interface NMS {
    BukkitTask startUntrackerTask(Plugin plugin, int tick);

    BukkitTask startUCheckTask(Plugin plugin, int tick);

    List<String> Entities(boolean isname);
}
