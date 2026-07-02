package dev.novinity.skywars.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Config {
    private File file;
    private File dir;
    private YamlConfiguration yaml;

    public Config(JavaPlugin plugin, String name) {
        dir = plugin.getDataFolder();

        if (!dir.exists()) {
            dir.mkdir();
        }

        file = new File(dir, name);

        if (!file.exists()) {
            plugin.saveResource(name, false);
        }

        yaml = new YamlConfiguration();

        try {
            yaml.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getConfig() {
        return yaml;
    }
}
