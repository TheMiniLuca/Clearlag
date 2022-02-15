package com.github.theminiluca.clear.lag.nms.v1_18_R1;

import com.github.theminiluca.clear.lag.nms.v1_18_R1.tasks.CheckTask;
import com.github.theminiluca.clear.lag.nms.v1_18_R1.tasks.UntrackerTask;
import com.github.theminiluca.clear.lag.nms.v1_18_R1.tasks.villager.MainTask;
import com.github.theminiluca.clear.lag.plugin.api.LatestNMS;
import com.github.theminiluca.clear.lag.plugin.api.NMS;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class NMSHandler implements LatestNMS {

    @Override
    public BukkitTask startUntrackerTask(Plugin plugin, int tick) {
        return new UntrackerTask().runTaskTimer(plugin, tick, tick);
    }

    @Override
    public BukkitTask startUCheckTask(Plugin plugin, int tick) {
        return new CheckTask().runTaskTimer(plugin, tick, tick);
    }

    @Override
    public List<String> Entities(boolean isname) {
        List<String> al = new ArrayList<>();
        for (EntityType type : EntityType.values()) {
            if (isname)
                al.add(type.getName());
            else
                al.add(type.name());
        }
        return al;
    }

    private BukkitTask task;

    @Override
    public void startTasks(Plugin plugin, int tick) {
        if (this.task != null) {
            this.task.cancel();
        }
        this.task = new MainTask(plugin).runTaskTimer(plugin, 0L, tick);
        this.task = Bukkit.getScheduler().runTaskTimer(plugin, new MainTask(plugin), 0L,
                tick);
    }
}
