package com.github.theminiluca.clear.lag.nms.v1_16_R3.tasks;

import com.github.theminiluca.clear.lag.plugin.Clearlag;
import com.github.theminiluca.clear.lag.plugin.api.Config;
import com.github.theminiluca.clear.lag.plugin.api.Language;
import com.github.theminiluca.clear.lag.plugin.api.util.ReflectionUtils;
import net.minecraft.server.v1_16_R3.*;
import net.minecraft.server.v1_16_R3.PlayerChunkMap.EntityTracker;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.github.theminiluca.clear.lag.plugin.Clearlag.logger;
import static com.github.theminiluca.clear.lag.plugin.Clearlag.removed;
import static com.github.theminiluca.clear.lag.plugin.api.Config.*;
import static com.github.theminiluca.clear.lag.plugin.api.Config.getList;

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
        if (((CraftServer) Bukkit.getServer()).getServer().recentTps[0] > getDouble(Config.Enum.TPS_LIMIT)) {
            return;
        }
        running = true;
        if (getBoolean(Config.Enum.ENABLE_ON_ALL_WORLDS)) {
            for (World world : Bukkit.getWorlds()) {
                untrackProcess(world.getName());
            }
        } else {
            for (String worldName : getList(Config.Enum.WORLDS)) {
                untrackProcess(worldName);
            }
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
                if (nmsEnt instanceof EntityPlayer || isEnableEntity(nmsEnt.getBukkitEntity().getType().name())) {
                    continue;
                }
                if (getBoolean(Config.Enum.IGNORE_ENTITY_NAME)) {
                    if (nmsEnt.getCustomName() != null) {
                        continue;
                    }
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
                    Clearlag.removed++;
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        for (int id : toRemove) {
            cps.playerChunkMap.trackedEntities.remove(id);
        }

        if (Config.getBoolean(Config.Enum.LOG_TO_CONSOLE)) {
            if(removed > 0) {
                logger.info(Language.getUntrackingLog(removed, worldName));
            }
        }
    }

    public static boolean isRunning() {
        return running;
    }

}
