package com.github.theminiluca.clear.lag.plugin.handle;

import org.bukkit.plugin.Plugin;

public class PluginVersion {

    private final String version;
    private final String latestVersion;

    public PluginVersion(String latestVersion, Plugin plugin) {
        this.version = plugin.getDescription().getVersion();
        this.latestVersion = latestVersion;
    }

    public String getVersion() {
        return version;
    }
    public String getLatestVersion() {
        return latestVersion;
    }


    public boolean isLatestVersion() {
        return Integer.parseInt(version.replace(".", "").replace("Beta", ""))
                > Integer.parseInt(latestVersion.replace(".", "").replace("Beta", ""));
    }

    public boolean isBeta() {
        return version.contains("Beta");
    }

}
