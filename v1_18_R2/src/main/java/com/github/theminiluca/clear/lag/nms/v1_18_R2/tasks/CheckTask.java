package com.github.theminiluca.clear.lag.nms.v1_18_R2.tasks;

import com.github.theminiluca.clear.lag.nms.v1_18_R2.NMSEntityTracker;
import com.github.theminiluca.clear.lag.nms.v1_18_R2.entityTick.EntityTickManager;
import com.github.theminiluca.clear.lag.plugin.handle.Config;
import net.minecraft.server.level.ChunkProviderServer;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CheckTask extends BukkitRunnable {

    @Override
    public void run() {
        if (UntrackerTask.isRunning()) {
            return;
        }
        if (Config.getInstance().getBoolean(Config.Enum.ENABLE_ON_ALL_WORLDS)) {
            for (World world : Bukkit.getWorlds()) {
                checkWorld(world.getName());
            }
        } else {
            for (String worldName : Config.getInstance().getList(Config.Enum.WORLDS)) {
                if (Bukkit.getWorld(worldName) == null) {
                    continue;
                }
                checkWorld(worldName);
            }
        }
    }

    public void checkWorld(String worldName) {
        WorldServer ws = ((CraftWorld) Objects.requireNonNull(Bukkit.getWorld(worldName))).getHandle();
        ChunkProviderServer cps = ws.k();

        Set<net.minecraft.world.entity.Entity> trackAgain = new HashSet<>();

        int d = Config.getInstance().getInt(Config.Enum.TRACKING_RANGE);
        for (Player player : Objects.requireNonNull(Bukkit.getWorld(worldName)).getPlayers()) {
            for (Entity ent : player.getNearbyEntities(d, d, d)) {
                net.minecraft.world.entity.Entity nms = ((CraftEntity) ent).getHandle();
                if (cps.a.J.containsKey(nms.ae()) || !nms.valid) {
                    continue;
                }
                trackAgain.add(nms);
            }
        }
        NMSEntityTracker.trackEntities(cps, trackAgain);
        if (Config.getInstance().getBoolean(Config.Enum.DISABLE_TICK_FOR_UNTRACKED_ENTITIES))
            EntityTickManager.getInstance().enableTicking(trackAgain);
    }


}