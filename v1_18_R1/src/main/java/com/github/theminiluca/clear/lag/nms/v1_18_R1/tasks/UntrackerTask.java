package com.github.theminiluca.clear.lag.nms.v1_18_R1.tasks;

import com.github.theminiluca.clear.lag.plugin.Clearlag;
import com.github.theminiluca.clear.lag.plugin.api.Config;
import com.github.theminiluca.clear.lag.plugin.api.Language;
import com.github.theminiluca.clear.lag.plugin.api.util.ReflectionUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkProviderServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.PlayerChunkMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.boss.EntityComplexPart;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.monster.EntityCreeper;
import net.minecraft.world.entity.npc.EntityVillager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.github.theminiluca.clear.lag.plugin.Clearlag.logger;
import static com.github.theminiluca.clear.lag.plugin.Clearlag.removed;
import static com.github.theminiluca.clear.lag.plugin.api.Config.*;

public class UntrackerTask extends BukkitRunnable {

    private static boolean running = false;

    private static Field trackerField;

    static {
        try {
            trackerField = ReflectionUtils.getClassPrivateField(PlayerChunkMap.EntityTracker.class, "c");
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

    private boolean isOnline(String s) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(s)) return true;
        }
        return false;
    }

    private void untrackProcess(String worldName) {
        if (Bukkit.getWorld(worldName) == null) {
            return;
        }
        //Set<net.minecraft.server.v1_14_R2.Entity> toRemove = new HashSet<>();
        Set<Integer> toRemove = new HashSet<>();
        WorldServer ws = ((CraftWorld) Objects.requireNonNull(Bukkit.getWorld(worldName))).getHandle();
        ChunkProviderServer cps = ws.k();

        try {
            for (PlayerChunkMap.EntityTracker et : cps.a.I.values()) {
                net.minecraft.world.entity.Entity nmsEnt = (net.minecraft.world.entity.Entity) trackerField.get(et);
                if (nmsEnt instanceof EntityPlayer || isEnableEntity(nmsEnt.getBukkitEntity().getType().name())) {
                    continue;
                }
                if (getBoolean(Config.Enum.IGNORE_ENTITY_NAME)) {
                    if (nmsEnt.Z() != null) {
                        continue;
                    }
                }
                boolean remove = false;
                if (et.f.size() == 0) {
                    remove = true;
                } else if (et.f.size() == 1) {
                    for (ServerPlayerConnection ep : et.f) {
                        if (!isOnline(ep.d().co())) {
                            remove = true;
                        }
                    }
                    if (!remove) {
                        continue;
                    }
                }
                if (remove) {
                    toRemove.add(nmsEnt.ae());
                    Clearlag.removed++;
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        for (int id : toRemove) {
            cps.a.I.remove(id);
        }

        if (Config.getBoolean(Config.Enum.LOG_TO_CONSOLE)) {
            if (removed > 0) {
                logger.info(Language.getUntrackingLog(removed, worldName));
            }
        }
    }

    public static boolean isRunning() {
        return running;
    }

}
