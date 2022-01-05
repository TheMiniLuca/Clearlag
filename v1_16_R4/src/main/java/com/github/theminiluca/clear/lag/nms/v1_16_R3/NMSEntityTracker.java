package com.github.theminiluca.clear.lag.nms.v1_16_R3;

import com.github.theminiluca.clear.lag.plugin.api.util.ReflectionUtils;
import net.minecraft.server.v1_16_R3.ChunkProviderServer;
import net.minecraft.server.v1_16_R3.PlayerChunkMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public final class NMSEntityTracker {

    private static Method addEntityMethod;

    static {
        try {
            addEntityMethod = ReflectionUtils.getPrivateMethod(PlayerChunkMap.class, "addEntity",
                    new Class[]{net.minecraft.server.v1_16_R3.Entity.class});
        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private NMSEntityTracker() {
    }

    public static void trackEntities(ChunkProviderServer cps, Set<net.minecraft.server.v1_16_R3.Entity> trackList) {
        try {
            for (net.minecraft.server.v1_16_R3.Entity entity : trackList) {
                addEntityMethod.invoke(cps.playerChunkMap, entity);
            }
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
