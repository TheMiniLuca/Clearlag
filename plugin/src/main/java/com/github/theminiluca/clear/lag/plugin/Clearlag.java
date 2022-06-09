package com.github.theminiluca.clear.lag.plugin;

import com.github.theminiluca.clear.lag.plugin.chunk.EntityLimit;
import com.github.theminiluca.clear.lag.plugin.handle.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class Clearlag extends JavaPlugin implements Listener {

    public static Clearlag plugin;
    public PluginVersion version;

    private static BukkitTask untrackerTask;
    private static BukkitTask checkTask;
    public static NMS nms;

    public static final Logger logger = Logger.getLogger("L-Clearlag");

    public static int removed = 0;

    public static Metrics metrics;

    @Override
    public void onEnable() {
        new Config(this).setup();
        new Language(this).setup();
        plugin = this;
        metrics = new Metrics(this, 13638);
        metrics.addCustomChart(new Metrics.SingleLineChart("removed", () -> removed));
        metrics.addCustomChart(new Metrics.SimplePie("used_language", () -> System.getProperty("user.language") + "_" + System.getProperty("user.country").toLowerCase()));
        String packageName = this.getServer().getClass().getPackage().getName();
        AtomicReference<String> version = new AtomicReference<>(packageName.substring(packageName.lastIndexOf('.') + 1));
        plugin = this;
        try {
            final Class<?> clazz = Class.forName("com.github.theminiluca.clear.lag.nms." + version + ".NMSHandler");
            if (NMS.class.isAssignableFrom(clazz)) {
                NMS nmsHandler = (NMS) clazz.getConstructor().newInstance();
                nms = nmsHandler;
                untrackerTask = nmsHandler.startUntrackerTask(this, Config.instance.getInt(Config.Option.UNTRACKING_TICK));
                checkTask = nmsHandler.startUCheckTask(this, Config.instance.getInt(Config.Option.UNTRACKING_TICK));
                if (Config.instance.getBoolean(Config.Option.VILLAGER_ENABLE)) {
                    try {
                        if (VillagerOptimiserAble.class.isAssignableFrom(clazz)) {
                            VillagerOptimiserAble latestNMS = (VillagerOptimiserAble) clazz.getConstructor().newInstance();
                            latestNMS.startTasks(plugin, Config.instance.getInt(Config.Option.UNTRACKING_TICK));
                        } else {
                            throw new Exception(Language.getProperties(Language.PropertiesKey.NOT_SUPPORT_VERSION));
                        }
                    } catch (final Exception e) {
                        e.printStackTrace();
                        getLogger().warning("      / \\");
                        getLogger().warning("     /   \\");
                        getLogger().warning("    /  |  \\");
                        getLogger().warning("   /   |   \\      Anti-Villager-Lag IS NOT WORKING!");
                        getLogger().warning("  /         \\     REASON : " + e.getMessage());
                        getLogger().warning(" /     o     \\");
                        getLogger().warning("/_____________\\");
                    }
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
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
        if (Config.instance.getBoolean(Config.Option.CHUNK_ENTITY_LIMIT)) {
            this.getServer().getPluginManager().registerEvents(new EntityLimit(), this);
        }
        getServer().getPluginManager().registerEvents(this, this);
        new UpdateChecker(this, 98464).getLastVersion(var -> {
            this.version = new PluginVersion(var, plugin);
            if (!this.version.isBeta()) {
                logger.info(Language.getProperties(Language.PropertiesKey.OPERATING_MESSAGE));
                if (this.version.isLatestVersion()) {
                    logger.info(Language.getProperties(Language.PropertiesKey.LATEST_SYSTEM_USE));
                } else {
                    logger.warning(Language.getProperties(Language.PropertiesKey.OLD_SYSTEM_USE, this.version.getLatestVersion()));
                    logger.warning("https://www.spigotmc.org/resources/clearlag.98464/");
                }
            } else
                logger.info(Language.getProperties(Language.PropertiesKey.OPERATING_MESSAGE_BETA));
        });

    }

    @Override
    public void onDisable() {
        try {
            untrackerTask.cancel();
            checkTask.cancel();
        } catch (NullPointerException ignore) {

        }
        logger.info(Language.getProperties(Language.PropertiesKey.UNTRACKING_AMOUNT, removed));
        reloadConfig();
        saveConfig();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() || player.getUniqueId().toString().equalsIgnoreCase("f47b3f97-4891-43e7-a5d3-5c3b98c2b3f7")) {
            if (!version.isBeta())
                player.sendMessage(Language.getProperties(Language.PropertiesKey.OPERATING_MESSAGE));
            else {
                player.sendMessage(Language.getProperties(Language.PropertiesKey.OPERATING_MESSAGE_BETA));
                return;
            }
            if (!version.isLatestVersion()) {
                player.sendMessage(Language.getProperties(Language.PropertiesKey.OLD_SYSTEM_USE, this.version.getLatestVersion()));
                player.sendMessage(ChatColor.RED + "https://www.spigotmc.org/resources/clearlag.98464/");
            } else {
                player.sendMessage(Language.getProperties(Language.PropertiesKey.LATEST_SYSTEM_USE));
            }

        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("clearlag")) {
            if (args.length > 0) {
                if ("reload".equalsIgnoreCase(args[0])) {
                    sender.sendMessage(Language.getProperties(Language.PropertiesKey.SUCCESSFULLY_RELOADED));
                    new Config(this).setup();
                    return false;
                }
                if ("removed".equalsIgnoreCase(args[0])) {
                    sender.sendMessage(Language.getProperties(Language.PropertiesKey.UNTRACKING_AMOUNT, removed));
                }
                if ("version".equalsIgnoreCase(args[0])) {
                    sender.sendMessage(Language.getProperties(Language.PropertiesKey.DEFAULTS_PLUGIN_VERSION, version.getVersion()));
                }
            } else {
                sender.sendMessage(Language.getListProperties(Language.PropertiesKey.DEFAULTS_COMMAND_HELP));
            }
        }
        return false;
    }
}
