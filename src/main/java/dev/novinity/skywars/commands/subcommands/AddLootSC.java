package dev.novinity.skywars.commands.subcommands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import dev.novinity.skywars.Skywars;
import dev.novinity.skywars.commands.SubCommand;
import dev.novinity.skywars.game.ChestManager;
import dev.novinity.skywars.utils.NumberUtils;
import dev.novinity.skywars.utils.RandomStringGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class AddLootSC extends SubCommand {
    @Override
    public String getName() {
        return "addloot";
    }

    @Override
    public String getDescription() {
        return "Add the item in your hand to a chest type's loot table";
    }

    @Override
    public String getSyntax() {
        return "/skywars addloot <chestType> <weight>";
    }

    @Override
    public String getRequiredPermission() {
        return "skywars.addloot";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player p) {
            if (args.length < 2) {
                p.sendMessage(ChatColor.RED + "You must specify a chest type! (island | side | center)");
                return;
            }

            if (args.length < 3) {
                p.sendMessage(ChatColor.RED + "You must specify a spawn chance from 0 - 1!");
                return;
            }

            ItemStack holding = p.getInventory().getItemInMainHand();
            if (holding.getType().equals(Material.AIR)) {
                p.sendMessage(ChatColor.RED + "You must be holding an item in your hand!");
                return;
            }

            String chestType = args[1].toUpperCase();
            if (!ChestManager.ChestType.hasType(chestType)) {
                p.sendMessage(ChatColor.RED + "Invalid chest type!");
                return;
            }

            Double weight = NumberUtils.tryParseDouble(args[2]);
            if (weight == null || weight > 1 || weight < 0) {
                p.sendMessage(ChatColor.RED + "Weight must be a number between 0 and 1!");
                return;
            }

            FileConfiguration config = Skywars.getInstance().getConfig();
            String id = RandomStringGenerator.generateRandomString(10);
            config.set("lootTables." + chestType + "." + id + ".weight", weight);
            config.set("lootTables." + chestType + "." + id + ".item", holding);

            Skywars.saveLootTablesConfig();
            p.sendMessage(ChatColor.GREEN + "Successfully added item to loot table! ID: " + id);
        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
        }
    }
}
