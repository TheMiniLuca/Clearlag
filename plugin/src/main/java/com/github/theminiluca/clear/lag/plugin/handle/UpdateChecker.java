package com.github.theminiluca.clear.lag.plugin.handle;

import com.github.theminiluca.clear.lag.plugin.Clearlag;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
    private final Clearlag plugin;
    private final int resourseId;

    public UpdateChecker(Clearlag plugin, int resourseId) {
        this.plugin = plugin;
        this.resourseId = resourseId;
    }

    public String getURL() {
        return "https://www.spigotmc.org/resources/clearlag.98464/";
    }
    public void getLastVersion(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourseId).openStream()) {
                Scanner scanner = new Scanner(inputStream);
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
