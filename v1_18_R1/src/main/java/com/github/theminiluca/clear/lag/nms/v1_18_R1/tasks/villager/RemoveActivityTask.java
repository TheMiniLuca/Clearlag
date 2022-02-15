package com.github.theminiluca.clear.lag.nms.v1_18_R1.tasks.villager;

import com.github.theminiluca.clear.lag.nms.v1_18_R1.tasks.villager.utils.ActivityUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitRunnable;

public class RemoveActivityTask extends BukkitRunnable {
    @Override
    public void run() {
        for(World world : Bukkit.getWorlds()) {
            for (LivingEntity entity : world.getLivingEntities()) {
                if (entity instanceof Villager) {
                    if(ActivityUtils.badCurrentActivity((Villager) entity)) {
                        ActivityUtils.setScheduleEmpty((Villager) entity);
                        ActivityUtils.setActivitiesNormal((Villager) entity);
                    }
                    ActivityUtils.replaceBadMemories((Villager) entity);
                }
            }
        }
    }
}
