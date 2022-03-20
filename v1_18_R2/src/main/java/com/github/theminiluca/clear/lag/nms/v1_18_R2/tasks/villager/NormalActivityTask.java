package com.github.theminiluca.clear.lag.nms.v1_18_R2.tasks.villager;

import com.github.theminiluca.clear.lag.nms.v1_18_R2.tasks.villager.utils.ActivityUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitRunnable;

public class NormalActivityTask extends BukkitRunnable {
    @Override
    public void run() {
        for(World world : Bukkit.getWorlds()) {
            for(LivingEntity entity : world.getLivingEntities()) {
                if(entity instanceof Villager) {
                    if(!ActivityUtils.wouldBeBadActivity((Villager) entity) && !ActivityUtils.isScheduleNormal((Villager) entity)) {
                        ActivityUtils.setScheduleNormal((Villager) entity);
                        ActivityUtils.setActivitiesNormal((Villager) entity);
                    }
                    ActivityUtils.clearPlaceholderMemories((Villager) entity);
                }
            }
        }
    }
}
