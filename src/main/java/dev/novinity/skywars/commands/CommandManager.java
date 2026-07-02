package dev.novinity.skywars.commands;


import dev.novinity.skywars.Properties;
import dev.novinity.skywars.Skywars;
import dev.novinity.skywars.commands.subcommands.AddLootSC;
import dev.novinity.skywars.commands.subcommands.CreateSpawnpointSC;
import dev.novinity.skywars.commands.subcommands.ReloadSC;
import dev.novinity.skywars.commands.subcommands.SetChestSC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements TabCompleter, CommandExecutor {

    public ArrayList<SubCommand> subCommands = new ArrayList<>();

    public CommandManager() {
        subCommands.add(new ReloadSC());
        subCommands.add(new SetChestSC());
        subCommands.add(new CreateSpawnpointSC());
        subCommands.add(new AddLootSC());
    }

    public ArrayList<SubCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            ArrayList<String> possibilities = new ArrayList<String>() {{
                for (SubCommand subCommand : subCommands) {
                    if (subCommand.getName().startsWith(args[0].toLowerCase())
                            && (subCommand.getRequiredPermission().isEmpty() || sender.hasPermission(subCommand.getRequiredPermission()))) {
                        add(subCommand.getName());
                    }
                }
            }};
            return new ArrayList<String>() {{
                for (String possibility : possibilities) {
                    if (possibility.startsWith(args[0].toLowerCase())) {
                        add(possibility);
                    }
                }
            }};
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length > 0) {
            for (int i = 0; i < getSubCommands().size(); i++) {
                if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName())) {
                    if (getSubCommands().get(i).getRequiredPermission().isEmpty() || sender.hasPermission(getSubCommands().get(i).getRequiredPermission())) {
                        getSubCommands().get(i).perform(sender, args);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Command does not exist!");
                    }
                }
            }
        } else if (args.length == 0) {
            sender.sendMessage(Skywars.PREFIX + ChatColor.translateAlternateColorCodes('&',
                    "&aVersion " + Properties.getProperty("version")));
        }

        return true;
    }
}
