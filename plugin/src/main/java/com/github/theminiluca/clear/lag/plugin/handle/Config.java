package com.github.theminiluca.clear.lag.plugin.handle;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static com.github.theminiluca.clear.lag.plugin.Clearlag.nms;

public class Config {

    private final JavaPlugin plugin;

    private final List<String> support_language = new ArrayList<>();

    public Config(JavaPlugin plugin) {
        this.plugin = plugin;
        support_language.add("ko_kr");
        support_language.add("en_us");
        instance = this;
    }

    public void setup() {
        File def = new File(plugin.getDataFolder().toString());
        if (!def.exists()) def.mkdir();
        File config = new File(plugin.getDataFolder(), "config.yml");
        File language_file = new File(plugin.getDataFolder() + "/lang");
        if (!language_file.exists())
            language_file.mkdir();
        String system_language = System.getProperty("user.language") + "_" + System.getProperty("user.country");
        for (String support_language : support_language) {
            InputStream in = plugin.getResource("lang/" + support_language + ".yml");
            try {
                File file = new File(language_file.toPath() + "/" + support_language + ".yml");
                if (!file.exists())
                    Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfiguration configuration = null;
        if (config.exists()) {
            plugin.reloadConfig();
            plugin.saveConfig();
            configuration = plugin.getConfig();
        }
        try {
            Files.copy(new File(plugin.getDataFolder() +
                    "/lang/" + system_language + ".yml").toPath(), new FileOutputStream(config));
        } catch (IOException e) {
            try {
                Files.copy(new File(plugin.getDataFolder() +
                        "/lang/en_us.yml").toPath(), new FileOutputStream(config));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (configuration != null) {
            for (Option option : Option.values()) {
                plugin.getConfig().set(option.getPath(), configuration.get(option.getPath(), option.getObject()));
            }
            plugin.getConfig().set("version", this.plugin.getDescription().getVersion());
            plugin.saveConfig();
            plugin.reloadConfig();
        }
    }


    public enum Option {
        LOG_TO_CONSOLE("log-to-console", Boolean.class),
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

        Option(String path, Object object) {
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


    public boolean getBoolean(Option e) {
        return plugin.getConfig().getBoolean(e.getPath());
    }

    public double getDouble(Option e) {
        return plugin.getConfig().getDouble(e.getPath());
    }

    public int getInt(Option e) {
        return plugin.getConfig().getInt(e.getPath());
    }

    public List<String> getList(Option e) {
        return plugin.getConfig().getStringList(e.getPath());
    }

    public String getEntityType(String name) {
        return nms.getEntities(false).stream().filter(s -> {
            assert s != null;
            return s.equalsIgnoreCase(name);
        }).findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public boolean isEnableEntity(String name) {
        return getList(Option.IGNORE_ENTITY_LIST).stream().anyMatch(s -> s.equalsIgnoreCase(getEntityType(name)));
    }

    public static Config instance;
}
