package com.github.theminiluca.clear.lag.nms.v1_16_R3.tasks;

import com.github.theminiluca.clear.lag.nms.v1_16_R3.entityTick.EntityTickManager;
import com.github.theminiluca.clear.lag.plugin.Clearlag;
import com.github.theminiluca.clear.lag.plugin.handle.Config;
import com.github.theminiluca.clear.lag.plugin.handle.Language;
import com.github.theminiluca.clear.lag.plugin.handle.util.ReflectionUtils;
import net.minecraft.server.v1_16_R3.ChunkProviderServer;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PlayerChunkMap.EntityTracker;
import net.minecraft.server.v1_16_R3.WorldServer;
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
        if (((CraftServer) Bukkit.getServer()).getServer().recentTps[0] > Config.instance.getDouble(Config.Option.TPS_LIMIT)) {
            return;
        }
        running = true;
        if (Config.instance.getBoolean(Config.Option.ENABLE_ON_ALL_WORLDS)) {
            for (World world : Bukkit.getWorlds()) {
                untrackProcess(world.getName());
            }
        } else {
            for (String worldName : Config.instance.getList(Config.Option.WORLDS)) {
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
        int removed = 0;
        try {
            for (EntityTracker et : cps.playerChunkMap.trackedEntities.values()) {
                net.minecraft.server.v1_16_R3.Entity nmsEnt = (net.minecraft.server.v1_16_R3.Entity) trackerField.get(et);
                if (nmsEnt instanceof EntityPlayer || Config.instance.isEnableEntity(nmsEnt.getBukkitEntity().getType().name())) {
                    continue;
                }
                if (Config.instance.getBoolean(Config.Option.IGNORE_ENTITY_NAME)) {
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
                    removed++;
                    Clearlag.removed++;
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        for (int id : toRemove) {
            cps.playerChunkMap.trackedEntities.remove(id);
            EntityTickManager.getInstance().disableTicking(ws.getEntity(id));
        }

        if (Config.instance.getBoolean(Config.Option.LOG_TO_CONSOLE)) {
            if(removed > 0) {
                logger.info(Language.getProperties(Language.PropertiesKey.UNTRACKING_LOG, removed, worldName));
            }
        }
    }

    public static boolean isRunning() {
        return running;
    }

}
