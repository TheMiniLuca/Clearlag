package com.github.theminiluca.clear.lag.nms.v1_16_R3.tasks;

import com.github.theminiluca.clear.lag.nms.v1_16_R3.entityTick.EntityTickManager;
import com.github.theminiluca.clear.lag.util.ReflectionUtils;
import net.minecraft.server.v1_16_R3.*;
import net.minecraft.server.v1_16_R3.PlayerChunkMap.EntityTracker;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class UntrackerTask extends BukkitRunnable {

    private static boolean running = false;

    private static Field trackerField;

    static {
        try {
            trackerField = ReflectionUtils.getClassPrivateField(EntityTracker.class, "tracker");
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"resource"})
    @Override
    public void run() {
        running = true;
        for (World worldName : Bukkit.getWorlds()) {
            untrackProcess(worldName.getName());
        }
        running = false;
    }

    private void untrackProcess(String worldName) {
        if (Bukkit.getWorld(worldName) == null) {
            return;
        }
        Set<Integer> toRemove = new HashSet<>();
        WorldServer ws = ((CraftWorld) Objects.requireNonNull(Bukkit.getWorld(worldName))).getHandle();
        ChunkProviderServer cps = ws.getChunkProvider();

        try {
            for (EntityTracker et : cps.playerChunkMap.trackedEntities.values()) {
                net.minecraft.server.v1_16_R3.Entity nmsEnt = (net.minecraft.server.v1_16_R3.Entity) trackerField.get(et);
                if (nmsEnt instanceof EntityPlayer || nmsEnt instanceof EntityEnderDragon || nmsEnt instanceof EntityComplexPart
                        || nmsEnt instanceof EntityVillager || nmsEnt instanceof EntityCreeper) {
                    continue;
                }
                if (nmsEnt instanceof EntityArmorStand && nmsEnt.getBukkitEntity().getCustomName() != null) {
                    continue;
                }
                boolean remove = false;
                if (et.trackedPlayers.size() == 0) {
                    remove = true;
                } else if (et.trackedPlayers.size() == 1) {
                    for (EntityPlayer ep : et.trackedPlayers) {
                        if (!ep.getBukkitEntity().isOnline()) {
                            remove = true;
                        }
                    }
                    if (!remove) {
                        continue;
                    }
                }
                if (remove) {
                    toRemove.add(nmsEnt.getId());
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        for (int id : toRemove) {
            cps.playerChunkMap.trackedEntities.remove(id);
            EntityTickManager.getInstance().disableTicking(ws.entitiesById.get(id));

        }
    }

    public static boolean isRunning() {
        return running;
    }

}
