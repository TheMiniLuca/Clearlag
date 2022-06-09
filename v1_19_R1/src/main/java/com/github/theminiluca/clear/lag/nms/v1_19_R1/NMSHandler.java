package com.github.theminiluca.clear.lag.nms.v1_19_R1;

import com.github.theminiluca.clear.lag.nms.v1_19_R1.tasks.CheckTask;
import com.github.theminiluca.clear.lag.nms.v1_19_R1.tasks.UntrackerTask;
import com.github.theminiluca.clear.lag.plugin.handle.NMS;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class NMSHandler implements NMS {

    @Override
    public BukkitTask startUntrackerTask(Plugin plugin, int tick) {
        return new UntrackerTask().runTaskTimer(plugin, tick, tick);
    }

    @Override
    public BukkitTask startUCheckTask(Plugin plugin, int tick) {
        return new CheckTask().runTaskTimer(plugin, tick, tick);
    }


    private static BukkitTask task;

//    @Override
//    public void startTasks(Plugin plugin, int tick) {
//        if (task != null) {
//            task.cancel();
//            return;
//        }
//        Bukkit.getScheduler().runTaskTimer(plugin, new MainTask(plugin), 0L,
//                 tick);
//    }

}
