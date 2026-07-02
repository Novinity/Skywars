package dev.novinity.skywars.commands.subcommands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import dev.novinity.skywars.Skywars;
import dev.novinity.skywars.commands.SubCommand;
import dev.novinity.skywars.utils.RandomStringGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class CreateSpawnpointSC extends SubCommand {
    @Override
    public String getName() {
        return "createspawnpoint";
    }

    @Override
    public String getDescription() {
        return "Create a new spawnpoint";
    }

    @Override
    public String getSyntax() {
        return "/skywars createspawnpoint";
    }

    @Override
    public String getRequiredPermission() {
        return "skywars.createspawnpoint";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player p) {
            Region selectedRegion;
            BlockVector3 pos1;
            BlockVector3 pos2;

            try {
                selectedRegion = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(p)).getSelection(BukkitAdapter.adapt(p.getWorld()));
                pos1 = selectedRegion.getMinimumPoint();
                pos2 = selectedRegion.getMaximumPoint();
            } catch (IncompleteRegionException e) {
                Skywars.getInstance().getLogger().log(Level.WARNING, "Could not get selected region");
                p.sendMessage(ChatColor.RED + "You do not have a valid area selected!");
                return;
            }

            String id = RandomStringGenerator.generateRandomString(10);

            Skywars.getMapsConfig().set(p.getWorld().getName() + ".spawnpoints." + id + ".pos1", new Location(p.getWorld(), pos1.x(), pos1.y(), pos1.z()));
            Skywars.getMapsConfig().set(p.getWorld().getName() + ".spawnpoints." + id + ".pos2", new Location(p.getWorld(), pos2.x(), pos2.y(), pos2.z()));
            Skywars.saveMapsConfig();

            p.sendMessage(ChatColor.GREEN + "Spawnpoint " + ChatColor.YELLOW + id + ChatColor.GREEN + " created!");
        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
        }
    }
}
