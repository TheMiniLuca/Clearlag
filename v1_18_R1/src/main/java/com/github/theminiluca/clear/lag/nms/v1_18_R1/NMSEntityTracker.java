package com.github.theminiluca.clear.lag.nms.v1_18_R1;

import com.github.theminiluca.clear.lag.plugin.api.util.ReflectionUtils;
import net.minecraft.server.level.ChunkProviderServer;
import net.minecraft.server.level.PlayerChunkMap;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public final class NMSEntityTracker {

    private static Method addEntityMethod;

    static {
        try {
            addEntityMethod = ReflectionUtils.getPrivateMethod(PlayerChunkMap.class, "a",
                    new Class[]{net.minecraft.world.entity.Entity.class});
        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private NMSEntityTracker() {
    }

    public static void trackEntities(ChunkProviderServer cps, Set<net.minecraft.world.entity.Entity> trackList) {
        try {
            for (net.minecraft.world.entity.Entity entity : trackList) {
                addEntityMethod.invoke(cps.a, entity);
            }
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
