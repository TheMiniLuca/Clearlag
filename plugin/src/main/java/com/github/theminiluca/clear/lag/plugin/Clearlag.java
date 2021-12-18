package com.github.theminiluca.clear.lag.plugin;

import com.github.theminiluca.clear.lag.api.NMS;
import com.github.theminiluca.clear.lag.plugin.update.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class Clearlag extends JavaPlugin implements Listener {

    public static Clearlag plugin;
    public String version;

    private BukkitTask untrackerTask;
    private BukkitTask checkTask;

    private final Logger logger = Logger.getLogger("Clearlag");

    @Override
    public void onEnable() {
        Metrics metrics = new Metrics(this, 13638);

        metrics.addCustomChart(new Metrics.SimplePie("Clearlag", () -> "Server"));
        String packageName = this.getServer().getClass().getPackage().getName();
        AtomicReference<String> version = new AtomicReference<>(packageName.substring(packageName.lastIndexOf('.') + 1));
        plugin = this;
        try {
            final Class<?> clazz = Class.forName("com.github.theminiluca.clear.lag.nms." + version + ".NMSHandler");
            if (NMS.class.isAssignableFrom(clazz)) {
                NMS nmsHandler = (NMS) clazz.getConstructor().newInstance();
                untrackerTask = nmsHandler.startUntrackerTask(this);
                checkTask = nmsHandler.startUCheckTask(this);
            }
        } catch (final Exception e) {
            getLogger().warning("      / \\");
            getLogger().warning("     /   \\");
            getLogger().warning("    /  |  \\");
            getLogger().warning("   /   |   \\         " + Bukkit.getVersion() + " IS NOT SUPPORT");
            getLogger().warning("  /         \\   CLEAR LAG MAY NOT WORK AS INTENDED");
            getLogger().warning(" /     o     \\");
            getLogger().warning("/_____________\\");
            this.getPluginLoader().disablePlugin(this);
            return;
        }
        getServer().getPluginManager().registerEvents(this, this);
        new UpdateChecker(this, 98464).getLastVersion(var -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(var)) {
                logger.info("this is plugin latest version");
            } else {
                logger.warning("this is plugin old version! please update! ( new version : " + var + " )");
                logger.warning("https://www.spigotmc.org/resources/clearlag.98464/");
            }
            this.version = var;
        });

    }

    @Override
    public void onDisable() {
        try {
            untrackerTask.cancel();
            checkTask.cancel();
        } catch (NullPointerException ignore) {

        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() || player.getUniqueId().toString().equalsIgnoreCase("f47b3f97-4891-43e7-a5d3-5c3b98c2b3f7")) {
            player.sendMessage(ChatColor.GREEN + "[CLEARLAG] The plugin is operating on that server!");
            if (!this.getDescription().getVersion().equalsIgnoreCase(this.version)) {
                player.sendMessage(ChatColor.RED + "this is plugin old version! please update! ( new version : " + this.version + " )");
                player.sendMessage(ChatColor.RED + "https://www.spigotmc.org/resources/clearlag.98464/");
            }

        }
    }


}
