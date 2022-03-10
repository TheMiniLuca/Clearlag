package com.github.theminiluca.clear.lag.nms.v1_18_R1.entityTick;

import com.github.theminiluca.clear.lag.plugin.Clearlag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.Set;

public class EntityTickManager implements Listener {

	private static EntityTickManager instance;

	private EntityTickManager() {
		Clearlag.plugin.getServer().getPluginManager().registerEvents(this, Clearlag.plugin);
	}

	public void disableTicking(net.minecraft.world.entity.Entity entity) {
		if(entity == null) {
			return;
		}
		if(!entity.valid) {
			return;
		}
		entity.activatedTick = -2147483648L;
		if(entity instanceof EntityInsentient) {
			//System.out.println("disable tick for insentient entity currently aware is = " + ((EntityInsentient)entity).aware + " should be true");
			((EntityInsentient)entity).aware = false;
		}
	}

	public void enableTicking(Set<net.minecraft.world.entity.Entity> entities) {
		for(net.minecraft.world.entity.Entity entity : entities) {
			if(entity == null) {
				continue;
			}
			if(!entity.valid) {
				continue;
			}
			entity.activatedTick = MinecraftServer.currentTick;
			if(entity instanceof EntityInsentient) {
				//System.out.println("enabling tick for insentient entity currently aware is = " + ((EntityInsentient)entity).aware + " should be false");
				((EntityInsentient)entity).aware = true;
			}
		}
	}

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		for(Entity entity : event.getChunk().getEntities()) {
			net.minecraft.world.entity.Entity nms = ((CraftEntity)entity).getHandle();
			if(nms instanceof EntityInsentient) {
				if(!((EntityInsentient)nms).aware) {
					((EntityInsentient)nms).aware = true;
				}
			}
		}
	}

	public static EntityTickManager getInstance() {
		if(instance == null) {
			instance = new EntityTickManager();
		}
		return instance;
	}
}
