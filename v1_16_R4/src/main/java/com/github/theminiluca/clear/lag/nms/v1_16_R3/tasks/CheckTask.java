package com.github.theminiluca.clear.lag.nms.v1_16_R3.tasks;

import com.github.theminiluca.clear.lag.nms.v1_16_R3.NMSEntityTracker;
import com.github.theminiluca.clear.lag.nms.v1_16_R3.entityTick.EntityTickManager;
import com.github.theminiluca.clear.lag.plugin.handle.Config;
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
        if (Config.instance.getBoolean(Config.Option.ENABLE_ON_ALL_WORLDS)) {
            for (World world : Bukkit.getWorlds()) {
                checkWorld(world.getName());
            }
        } else {
            for (String worldName : Config.instance.getList(Config.Option.WORLDS)) {
                if (Bukkit.getWorld(worldName) == null) {
                    continue;
                }
                checkWorld(worldName);
            }
        }
    }

    public void checkWorld(String worldName) {
        WorldServer ws = ((CraftWorld) Objects.requireNonNull(Bukkit.getWorld(worldName))).getHandle();
        ChunkProviderServer cps = ws.getChunkProvider();

        Set<net.minecraft.server.v1_16_R3.Entity> trackAgain = new HashSet<>();

        int d = Config.instance.getInt(Config.Option.TRACKING_RANGE);
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
        if (Config.instance.getBoolean(Config.Option.DISABLE_TICK_FOR_UNTRACKED_ENTITIES))
            EntityTickManager.getInstance().enableTicking(trackAgain);

    }

}