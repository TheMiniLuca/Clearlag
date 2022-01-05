package com.github.theminiluca.clear.lag.plugin;

import com.github.theminiluca.clear.lag.plugin.api.Config;
import com.github.theminiluca.clear.lag.plugin.api.Language;
import com.github.theminiluca.clear.lag.plugin.api.NMS;
import com.github.theminiluca.clear.lag.plugin.api.UpdateChecker;
import net.minecraft.server.v1_13_R2.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class Clearlag extends JavaPlugin implements Listener {

    public static Clearlag plugin;
    public String version;

    private static BukkitTask untrackerTask;
    private static BukkitTask checkTask;
    public static NMS nms;

    public static final Logger logger = Logger.getLogger("Clearlag");

    public static int removed = 0;

    public static Metrics metrics;
    public static String language;

    @Override
    public void onEnable() {
        reloadConfig();
        saveConfig();
        Config.setup(this);
        plugin = this;
        language = System.getProperty("user.language");
        metrics = new Metrics(this, 13638);
        metrics.addCustomChart(new Metrics.SingleLineChart("removed", () -> {
            return removed;
        }));
        String packageName = this.getServer().getClass().getPackage().getName();
        AtomicReference<String> version = new AtomicReference<>(packageName.substring(packageName.lastIndexOf('.') + 1));
        plugin = this;
        try {
            final Class<?> clazz = Class.forName("com.github.theminiluca.clear.lag.nms." + version + ".NMSHandler");
            if (NMS.class.isAssignableFrom(clazz)) {
                NMS nmsHandler = (NMS) clazz.getConstructor().newInstance();
                nms = nmsHandler;
                untrackerTask = nmsHandler.startUntrackerTask(this, Config.getInt(Config.Enum.UNTRACKING_TICK));
                checkTask = nmsHandler.startUCheckTask(this, Config.getInt(Config.Enum.UNTRACKING_TICK));
            }
        } catch (final Exception e) {
            getLogger().warning("      / \\");
            getLogger().warning("     /   \\");
            getLogger().warning("    /  |  \\");
            getLogger().warning("   /   |   \\         " + version + " IS NOT SUPPORT");
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
        logger.info(ChatColor.GREEN + "✔ While this server was running, " + ChatColor.UNDERLINE + "" + removed + ChatColor.GREEN + " canceled tracking of entities!");
        reloadConfig();
        saveConfig();
        Config.setup(this);
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("clearlag")) {
            if (args.length > 0) {
                if ("reload".equalsIgnoreCase(args[0])) {
                    sender.sendMessage(ChatColor.GREEN + "[CLEARLAG] Config.yml was successfully reloaded.");
                    reloadConfig();
                    saveConfig();
                    Config.setup(this);
                }
                if ("removed".equalsIgnoreCase(args[0])) {
                    sender.sendMessage(ChatColor.GREEN + "✔ While this server was running, " + ChatColor.UNDERLINE + "" + removed + ChatColor.GREEN + " canceled tracking of entities!");
                }
            } else {
                sender.sendMessage("/clearlag reload - config reload");
                sender.sendMessage("/clearlag removed - View the number of entities whose tracking has been canceled so far.");
            }
        }
        return false;
    }
}
