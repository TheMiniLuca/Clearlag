package com.github.theminiluca.clear.lag.plugin.handle;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.github.theminiluca.clear.lag.plugin.Clearlag.*;

public class Config {

    private static final String line = "\n";

    public static void setup(JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        BufferedWriter fw = null;
        try {
            fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(plugin.getDataFolder() + File.separator + "config.yml"), StandardCharsets.UTF_8));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        try {
            StringBuilder sb = new StringBuilder();
            sb.append("#Clearlag Plugin\n");
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dates = sdf.format(cal.getTime());
            sb.append("#Last Reload Date : ").append(dates).append("\n");
            sb.append(line);
            sb.append(line);
            sb.append(line);
            sb.append("# Should we notify the console when we interact with entities?\n" +
                    "# - Default value(s): false\n"
            );
            if (!config.isSet("log-to-console"))
                sb.append("log-to-console: false\n");
            else
                sb.append("log-to-console: ").append(config.getBoolean("log-to-console")).append("\n");
            sb.append(line);
            sb.append(line);
            /*
            sb.append("#Set the language of the plugin.\n"
                    + "#defauls : Set it to the window system language.\n"
                    + "#ko : 한국어\n"
                    + "#en : English\n"
            );
            if (!config.isSet("language"))
                sb.append("language: defaults\n");
            else
                sb.append("language: ").append(config.getString("language")).append("\n");
             */
            sb.append("# Should we disable tick operations of un-tracked entities?\n" +
                    "# Note 1: this option only works for +1.16.1 servers\n" +
                    "# Note 2: this option is experimental and could contain errors\n" +
                    "# - Default value(s): true\n");
            if (!config.isSet("disable-tick-for-untracked-entities"))
                sb.append("disable-tick-for-untracked-entities: true\n");
            else {
                sb.append("disable-tick-for-untracked-entities: ").append(config.getBoolean("disable-tick-for-untracked-entities")).append("\n");
            }
            sb.append(line);
            sb.append(line);
            sb.append("# How low should the server's TPS be before we do anything?\n" +
                    "# - Note: Setting this value above 20 will skip this check, allowing the tasks to run 24/7.\n" +
                    "# - Default value(s): 20.0\n"
            );
            if (!config.isSet("tps-limit"))
                sb.append("tps-limit: 20.0\n");
            else {
                sb.append("tps-limit: ").append(config.getDouble("tps-limit")).append("\n");
            }
            sb.append(line);
            sb.append(line);
            sb.append("# What entities should we ignore?\n"
                    + "# - Default value(s):\n"
                    + "#   - creeper\n"
                    + "#   - villager\n"
                    + "#   - ender_dragon\n"
                    + "#   - armor_stand\n");
            if (!config.isSet("ignore-entity-list")) {
                sb.append("ignore-entity-list:\n");
                sb.append(" - creeper\n");
                sb.append(" - villager\n");
                sb.append(" - ender_dragon\n");
                sb.append(" - armor_stand\n");
            } else {
                sb.append("ignore-entity-list: ");
                for (String s : config.getStringList("ignore-entity-list")) {
                    sb.append("\n").append(" - ").append(s);
                }
            }
            sb.append(line);
            sb.append(line);
            sb.append("# Should we ignore an entity with a name?\n" +
                    "# - Default value(s): true\n");
            if (!config.isSet("ignore-entity-name")) {
                sb.append("ignore-entity-name: true\n");
            } else {
                sb.append("ignore-entity-name: ").append(config.getBoolean("ignore-entity-name")).append("\n");
            }
            sb.append(line);
            sb.append(line);
            sb.append("# How far (in blocks) should we look for players near un-tracked entities?\n" +
                    "# - Default value(s): 50\n");
            if (!config.isSet("tracking-range")) {
                sb.append("tracking-range: 50\n");
            } else {
                sb.append("tracking-range: ").append(config.getInt("tracking-range")).append("\n");
            }
            sb.append(line);
            sb.append(line);
            sb.append("# How often (in ticks) should we check for \"lingering\" entities?\n" +
                    "# - Default value(s): 1000\n"
            );
            if (!config.isSet("untrack-ticks")) {
                sb.append("untrack-ticks: 1000\n");
            } else {
                sb.append("untrack-ticks: ").append(config.getInt("untrack-ticks")).append("\n");
            }
            sb.append(line);
            sb.append(line);
            sb.append("# Should the plugin perform tasks on all worlds?\n" +
                    "# Note: if this is set to true, the option \"worlds\" will be ignored\n" +
                    "# - Default value(s): true\n");
            if (!config.isSet("enable-on-all-worlds")) {
                sb.append("enable-on-all-worlds: true\n");
            } else {
                sb.append("enable-on-all-worlds: ").append(config.getBoolean("enable-on-all-worlds")).append("\n");
            }
            sb.append(line);
            sb.append(line);
            sb.append("# What worlds should we perform our tasks on?\n" +
                    "# - Default value(s): \n"
                    + "#   - world\n"
                    + "#   - world_nether\n"
                    + "#   - world_the_end\n");
            if (!config.isSet("worlds")) {
                sb.append("worlds:\n");
                sb.append(" - world\n");
                sb.append(" - world_nether\n");
                sb.append(" - world_the_end\n");
            } else {
                sb.append("worlds: ");
                for (String s : config.getStringList("worlds")) {
                    sb.append("\n").append(" - ").append(s);
                }
            }
            sb.append(line);
            sb.append(line);
            sb.append("# Beta version\n");
            sb.append("# https://www.spigotmc.org/resources/villager-optimiser-1-14-2-1-16-5.68517/\n");
            if (!config.isSet("villager-enable")) {
                sb.append("villager-enable: false\n");
            } else {
                sb.append("villager-enable: ").append(config.getBoolean("villager-enable")).append("\n");
            }


            sb.append("# - Default value(s): 600\n");

            if (!config.isSet("ticks-per-allow-search")) {
                sb.append("ticks-per-allow-search: 600\n");
            } else {
                sb.append("ticks-per-allow-search: ").append(config.getInt("ticks-per-allow-search")).append("\n");
            }
            sb.append(line);
            sb.append(line);
            sb.append(line);
            sb.append("version: ").append(plugin.getDescription().getVersion()).append("\n");

            assert fw != null;
            fw.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fw.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.reloadConfig();

    }

    public enum Enum {
        LOG_TO_CONSOLE("log-to-console", Boolean.class),
        LANGUAGE("language", String.class),
        TPS_LIMIT("tps-limit", Double.class),
        IGNORE_ENTITY_LIST("ignore-entity-list", ArrayList.class),
        IGNORE_ENTITY_NAME("ignore-entity-name", Boolean.class),
        TRACKING_RANGE("tracking-range", Integer.class),
        UNTRACKING_TICK("untrack-ticks", Integer.class),
        ENABLE_ON_ALL_WORLDS("enable-on-all-worlds", ArrayList.class),
        WORLDS("worlds", ArrayList.class),
        DISABLE_TICK_FOR_UNTRACKED_ENTITIES("disable-tick-for-untracked-entities", Boolean.class),
        VILLAGER_ENABLE("villager-enable", Boolean.class),
        TICKS_PER_ALLOW_SEARCH("ticks-per-allow-search", Integer.class),
        ;
        private final String path;
        private final Object object;

        Enum(String path, Object object) {
            this.path = path;
            this.object = object;
        }

        public String getPath() {
            return path;
        }

        public Object getObject() {
            return object;
        }
    }


    public boolean getBoolean(Enum e) {
        return plugin.getConfig().getBoolean(e.getPath());
    }

    public double getDouble(Enum e) {
        return plugin.getConfig().getDouble(e.getPath());
    }

    public int getInt(Enum e) {
        return plugin.getConfig().getInt(e.getPath());
    }

    public List<String> getList(Enum e) {
        return plugin.getConfig().getStringList(e.getPath());
    }

    public String getEntityType(String name) {
        return nms.getEntities(false).stream().filter(s -> {
            assert s != null;
            return s.equalsIgnoreCase(name);
        }).findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public boolean isEnableEntity(String name) {
        return getList(Enum.IGNORE_ENTITY_LIST).stream().anyMatch(s -> s.equalsIgnoreCase(getEntityType(name)));
    }

    private static Config instance;

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }
}
