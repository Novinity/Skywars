package dev.novinity.skywars.utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtils {
    public static void PlaySoundForAll(Sound sound, Float volume, Float pitch) {
        volume = volume == null ? 1 : volume;
        pitch = pitch == null ? 1 : pitch;
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }
}
