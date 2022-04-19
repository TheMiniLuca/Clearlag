package com.github.theminiluca.clear.lag.plugin.handle;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;

public class Language {

    public static Properties properties;

    public Plugin plugin;

    public Language(Plugin plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        File def = new File(plugin.getDataFolder().toString());
        File messagesFile = new File(plugin.getDataFolder() + "\\messages.properties");
        if (!def.exists()) def.mkdir();
        properties = new Properties();
        String system_language = System.getProperty("user.language") + "_" + System.getProperty("user.country");
        try {
            properties.load(new FileInputStream(messagesFile.toString()));
        } catch (IOException e) {
            if (!messagesFile.exists()) {
                InputStream in_data = plugin.getResource("lang/messages/" + system_language + ".properties");
                if (in_data != null) {
                    try {
                        Files.copy(in_data, messagesFile.toPath());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }


    public static String getProperties(PropertiesKey key) {
        String value = properties.getProperty(key.property());
        if (value == null || value.isEmpty()) return "";
        value = new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    public static boolean isProperties(PropertiesKey key) {
        String value = properties.getProperty(key.property());
        if (value == null || value.isEmpty()) return false;
        value = new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        return Boolean.parseBoolean(value);
    }

    public enum PropertiesKey {
        UNTRACKING_LOG("untracking.log");

        private final String property;

        PropertiesKey(String property) {
            this.property = property;
        }


        public String property() {
            return property;
        }
    }

}
