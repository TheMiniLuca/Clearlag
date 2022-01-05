package com.github.theminiluca.clear.lag.plugin.api;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Language {

    private static final String untracking_log = "Untracked %i entities in %s";

    public static String getUntrackingLog(int i, String s) {
        return untracking_log.replace("%i", String.valueOf(i)).replace("%s", s);
    }

}
