package dev.novinity.skywars;

import dev.novinity.skywars.commands.CommandManager;
import dev.novinity.skywars.config.Config;
import dev.novinity.skywars.game.GameManager;
import dev.novinity.skywars.listeners.PlayerListeners;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Skywars extends JavaPlugin {

    public static final String PREFIX = "&c[SKYWARS] ";

    private static Skywars instance;

    private GameManager gameManager;

    private static Config kitsConfig;
    private static Config mapsConfig;
    private static Config lootTablesConfig;

    @Override
    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        kitsConfig = new Config(this, "kits.yml");
        mapsConfig = new Config(this, "maps.yml");
        lootTablesConfig = new Config(this, "loot_tables.yml");

        getServer().getPluginCommand("skywars").setExecutor(new CommandManager());
        getServer().getPluginCommand("skywars").setTabCompleter(new CommandManager());

        getServer().getPluginManager().registerEvents(new PlayerListeners(), this);

        gameManager = new GameManager();

        if (getConfig().getBoolean("allowStart", false)) {
            gameManager.setupGame(getServer().getWorld("world"));
        }
    }

    public static Skywars getInstance() {
        return instance;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public static YamlConfiguration getKitsConfig() { return kitsConfig.getConfig(); }
    public static YamlConfiguration getMapsConfig() { return mapsConfig.getConfig(); }
    public static YamlConfiguration getLootTablesConfig() { return lootTablesConfig.getConfig(); }

    public static void saveKitsConfig() { kitsConfig.save(); }
    public static void saveMapsConfig() { mapsConfig.save(); }
    public static void saveLootTablesConfig() { lootTablesConfig.save(); }

    public static void reloadKitsConfig() { kitsConfig = new Config(Skywars.getInstance(), "kits.yml"); }
    public static void reloadMapsConfig() { kitsConfig = new Config(Skywars.getInstance(), "maps.yml"); }
    public static void reloadLootTablesConfig() { kitsConfig = new Config(Skywars.getInstance(), "loot_tables.yml"); }
}
