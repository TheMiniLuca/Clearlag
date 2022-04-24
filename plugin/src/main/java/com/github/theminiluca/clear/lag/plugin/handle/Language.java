package com.github.theminiluca.clear.lag.plugin.handle;

import com.github.theminiluca.clear.lag.plugin.Clearlag;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

public class Language {

    public static Properties properties;

    public Plugin plugin;

    public Language(Plugin plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        File def = new File(plugin.getDataFolder().toString());
        if (!def.exists()) def.mkdir();
        properties = new Properties();
        String system_language = System.getProperty("user.language") + "_" + System.getProperty("user.country").toLowerCase();
        try {
            properties.load(plugin.getResource("lang/messages/" + system_language + ".properties"));
        } catch (IOException | NullPointerException e) {
            try {
                properties.load(plugin.getResource("lang/messages/en_us.properties"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    public static String getProperties(PropertiesKey key) {
        String value = properties.getProperty(key.property());
        if (value == null || value.isEmpty()) return "";
        return ChatColor.translateAlternateColorCodes('&', value);
    }
    public static String[] getListProperties(PropertiesKey key) {
        String value = properties.getProperty(key.property());
        String[] strings = value.split(";,");
        int i = 0;
        for (String s : strings) {
            strings[i] = ChatColor.translateAlternateColorCodes('&', s);
            i++;
        }
        return strings;
    }
    public static String getProperties(PropertiesKey key, Object... args) {
        String value = properties.getProperty(key.property());
        if (value == null || value.isEmpty()) return "";
        return ChatColor.translateAlternateColorCodes('&', String.format(value, args));
    }

    public static boolean isProperties(PropertiesKey key) {
        String value = properties.getProperty(key.property());
        if (value == null || value.isEmpty()) return false;
        return Boolean.parseBoolean(value);
    }

    public enum PropertiesKey {
        UNTRACKING_LOG("untracking.log"),
        OLD_SYSTEM_USE("old.system.use"),
        LATEST_SYSTEM_USE("latest.system.use"),
        NOT_SUPPORT_VERSION("not.support.version"),
        DEFAULTS_PLUGIN_VERSION("defaults.plugin.version"),
        DEFAULTS_COMMAND_HELP("defaults.command.help", ArrayList.class),
        OPERATING_MESSAGE("operating.message"),
        SUCCESSFULLY_RELOADED("reload.successfully"),
        OPERATING_MESSAGE_BETA("operating.message.beta"),
        UNTRACKING_AMOUNT("untracking.amount");

        private final String property;
        private final Class<?> clazz;

        PropertiesKey(String property) {
            this.property = property;
            this.clazz = String.class;
        }
        PropertiesKey(String property, Class<?> clazz) {
            this.property = property;
            this.clazz = clazz;
        }


        public String property() {
            return property;
        }
        public Class<?> aClass() {
            return clazz;
        }
    }

}
