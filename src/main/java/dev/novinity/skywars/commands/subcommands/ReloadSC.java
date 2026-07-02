package dev.novinity.skywars.commands.subcommands;

import dev.novinity.skywars.Skywars;
import dev.novinity.skywars.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadSC extends SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload the config";
    }

    @Override
    public String getSyntax() {
        return "/skywars reload";
    }

    @Override
    public String getRequiredPermission() {
        return "skywars.reload";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Skywars.getInstance().reloadConfig();
        Skywars.reloadKitsConfig();
        Skywars.reloadMapsConfig();
        Skywars.reloadLootTablesConfig();

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Skywars.PREFIX + "&aReloaded config!"));
    }
}
