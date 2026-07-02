package dev.novinity.skywars.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public class TitleUtils {
    public static void ShowTitle(Player player, String title, String subtitle, Integer fadeIn, Integer stay, Integer fadeOut) {
        fadeIn = fadeIn == null ? 0 : fadeIn;
        stay = stay == null ? 60 : stay;
        fadeOut = fadeOut == null ? 0 : fadeOut;
        subtitle = subtitle == null ? "" : subtitle;
        player.sendTitle(ChatColor.translateAlternateColorCodes('&', title), ChatColor.translateAlternateColorCodes('&', subtitle), 0, 60, 0);
    }

    public static void ShowTitleForAll(String title, String subtitle, Integer fadeIn, Integer stay, Integer fadeOut) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ShowTitle(player, title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public static void SendBroadcast(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }
}
