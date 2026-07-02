package dev.novinity.skywars.utils;

import org.bukkit.Location;

public class LocationUtils {
    public static boolean isLocationPosSame(Location loc1, Location loc2) {
        if (loc1.getWorld() != loc2.getWorld()) return false;
        if (loc1.getX() != loc2.getX()) return false;
        if (loc1.getY() != loc2.getY()) return false;
        if (loc1.getZ() != loc2.getZ()) return false;
        return true;
    }
}
