package dev.novinity.skywars.commands.subcommands;

import dev.novinity.skywars.Skywars;
import dev.novinity.skywars.commands.SubCommand;
import dev.novinity.skywars.game.ChestManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetChestSC extends SubCommand {
    @Override
    public String getName() {
        return "setchest";
    }

    @Override
    public String getDescription() {
        return "Set the type of the chest you're facing";
    }

    @Override
    public String getSyntax() {
        return "/skywars setchest <type>";
    }

    @Override
    public String getRequiredPermission() {
        return "skywars.setchest";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player p) {
            Block lookingAt = p.getTargetBlockExact(5);
            if (lookingAt == null || lookingAt.getType() != Material.CHEST) {
                p.sendMessage(ChatColor.RED + "You must be looking at a chest to do this.");
                return;
            }

            String key = lookingAt.getX() + "-" +  lookingAt.getY() + "-" + lookingAt.getZ();
            if (args.length == 1) {
                Skywars.getMapsConfig().set(p.getWorld().getName() + ".chests." + key, null);
            } else {
                if (!ChestManager.ChestType.hasType(args[1])) {
                    p.sendMessage(ChatColor.RED + "Invalid chest type. Must be: island | side | center");
                    return;
                }

                Skywars.getMapsConfig().set(p.getWorld().getName() + ".chests." + key + ".type", args[1].toUpperCase());
                Skywars.getMapsConfig().set(p.getWorld().getName() + ".chests." + key + ".location", lookingAt.getLocation());
            }

            Skywars.saveMapsConfig();
            p.sendMessage(ChatColor.GREEN + "Successfully set the chest type.");
        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
        }
    }
}
