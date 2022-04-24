package com.github.theminiluca.clear.lag.plugin.chunk;

import com.github.theminiluca.clear.lag.plugin.Clearlag;
import com.github.theminiluca.clear.lag.plugin.handle.Config;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

public class EntityLimit implements Listener {


    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {

        Chunk chunk = event.getLocation().getChunk();
        Entity e =  event.getEntity();
        if (e instanceof Item) return;
        HashMap<String, Integer> entities = new HashMap<>();
        for (Entity entity : chunk.getEntities()) {
            if (!(entity instanceof Player) && !(entity instanceof Item)) {
                entities.put(entity.getType().name(), entities.getOrDefault(entity.getType().name(), 0) + 1);
                entities.put("all", entities.getOrDefault("all", 0) + 1);
            }
        }
        if (Config.instance.getLimitEntities().containsKey("all")) {
            if (entities.getOrDefault("all", 0) > Config.instance.getLimitEntities().getOrDefault("all", 0)) {
                event.setCancelled(true);
                return;
            }
        }
        if (entities.getOrDefault(e.getType().name(), 0) > Config.instance.getLimitEntities().getOrDefault(e.getType().name(), 0)) {
            event.setCancelled(true);
        }
    }
}
