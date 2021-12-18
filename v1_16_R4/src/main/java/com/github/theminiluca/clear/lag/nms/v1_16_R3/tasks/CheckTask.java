package com.github.theminiluca.clear.lag.nms.v1_16_R3.tasks;

import com.github.theminiluca.clear.lag.nms.v1_16_R3.NMSEntityTracker;
import com.github.theminiluca.clear.lag.nms.v1_16_R3.entityTick.EntityTickManager;
import net.minecraft.server.v1_16_R3.ChunkProviderServer;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
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
        for (World worldName : Bukkit.getWorlds()) {
            checkWorld(worldName.getName());
        }
    }

    public void checkWorld(String worldName) {
        WorldServer ws = ((CraftWorld) Objects.requireNonNull(Bukkit.getWorld(worldName))).getHandle();
        ChunkProviderServer cps = ws.getChunkProvider();

        Set<net.minecraft.server.v1_16_R3.Entity> trackAgain = new HashSet<>();

        int d = 50;
        for (Player player : Objects.requireNonNull(Bukkit.getWorld(worldName)).getPlayers()) {
            for (Entity ent : player.getNearbyEntities(d, d, d)) {
                net.minecraft.server.v1_16_R3.Entity nms = ((CraftEntity) ent).getHandle();
                if (cps.playerChunkMap.trackedEntities.containsKey(nms.getId()) || !nms.valid) {
                    continue;
                }
                trackAgain.add(nms);
            }
        }
        NMSEntityTracker.trackEntities(cps, trackAgain);
        EntityTickManager.getInstance().enableTicking(trackAgain);

    }

}