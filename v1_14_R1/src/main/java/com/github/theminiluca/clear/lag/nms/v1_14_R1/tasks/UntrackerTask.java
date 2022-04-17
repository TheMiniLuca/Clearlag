package com.github.theminiluca.clear.lag.nms.v1_14_R1.tasks;

import com.github.theminiluca.clear.lag.plugin.handle.Config;
import com.github.theminiluca.clear.lag.plugin.handle.Language;
import com.github.theminiluca.clear.lag.plugin.handle.util.ReflectionUtils;
import net.minecraft.server.v1_14_R1.ChunkProviderServer;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.PlayerChunkMap;
import net.minecraft.server.v1_14_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
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
            trackerField = ReflectionUtils.getClassPrivateField(PlayerChunkMap.EntityTracker.class, "tracker");
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"resource"})
    @Override
    public void run() {
        if (((CraftServer) Bukkit.getServer()).getServer().recentTps[0] > Config.getInstance().getDouble(Config.Enum.TPS_LIMIT)) {
            return;
        }
        running = true;
        if (Config.getInstance().getBoolean(Config.Enum.ENABLE_ON_ALL_WORLDS)) {
            for (World world : Bukkit.getWorlds()) {
                untrackProcess(world.getName());
            }
        } else {
            for (String worldName : Config.getInstance().getList(Config.Enum.WORLDS)) {
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
            for (PlayerChunkMap.EntityTracker et : cps.playerChunkMap.trackedEntities.values()) {
                net.minecraft.server.v1_14_R1.Entity nmsEnt = (net.minecraft.server.v1_14_R1.Entity) trackerField.get(et);
                if (nmsEnt instanceof EntityPlayer || Config.getInstance().isEnableEntity(nmsEnt.getBukkitEntity().getType().name())) {
                    continue;
                }
                if (Config.getInstance().getBoolean(Config.Enum.IGNORE_ENTITY_NAME)) {
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
                    //System.out.println("untracked: " + nmsEnt.getBukkitEntity().getType().name());
                    toRemove.add(nmsEnt.getId());
                    removed++;
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        for (int id : toRemove) {
            cps.playerChunkMap.trackedEntities.remove(id);
        }

        if (Config.getInstance().getBoolean(Config.Enum.LOG_TO_CONSOLE)) {
            if(removed > 0) {
                logger.info(Language.getUntrackingLog(removed, worldName));
            }
        }
    }

    public static boolean isRunning() {
        return running;
    }

}
