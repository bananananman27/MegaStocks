package com.weston.megastocks.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public final class LocationKey {
    private LocationKey() {}

    public static String of(Location location) {
        return location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
    }

    public static Location parse(String key) {
        String[] parts = key.split(":");
        if (parts.length != 4) return null;
        World world = Bukkit.getWorld(parts[0]);
        if (world == null) return null;
        try {
            return new Location(world, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static String pretty(String key) {
        String[] p = key.split(":");
        if (p.length != 4) return key;
        return p[0] + " " + p[1] + ", " + p[2] + ", " + p[3];
    }
}
