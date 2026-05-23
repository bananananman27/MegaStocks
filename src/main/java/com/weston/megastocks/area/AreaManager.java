package com.weston.megastocks.area;

import com.weston.megastocks.MegaStocksPlugin;
import com.weston.megastocks.data.PlayerData;
import com.weston.megastocks.machine.MachineData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class AreaManager {
    public static final String DEFAULT_AREA = "default";
    private final MegaStocksPlugin plugin;

    public AreaManager(MegaStocksPlugin plugin) {
        this.plugin = plugin;
    }

    public String sanitize(String raw) {
        if (raw == null || raw.isBlank()) return DEFAULT_AREA;
        String out = raw.toLowerCase(Locale.ROOT).trim().replaceAll("[^a-z0-9_\\-]+", "_");
        out = out.replaceAll("_+", "_").replaceAll("^_+|_+$", "");
        if (out.isBlank()) out = DEFAULT_AREA;
        if (out.length() > 24) out = out.substring(0, 24);
        return out;
    }

    public String displayName(String areaId) {
        String id = sanitize(areaId);
        if (DEFAULT_AREA.equals(id)) return "Default Area";
        String lower = id.replace('_', ' ').replace('-', ' ');
        StringBuilder out = new StringBuilder();
        for (String part : lower.split(" ")) {
            if (part.isBlank()) continue;
            if (out.length() > 0) out.append(' ');
            out.append(Character.toUpperCase(part.charAt(0))).append(part.length() > 1 ? part.substring(1) : "");
        }
        return out.length() == 0 ? "Default Area" : out.toString();
    }

    public void ensureArea(Player player, String rawName) {
        PlayerData data = plugin.data().player(player.getUniqueId(), player.getName());
        data.areas().add(sanitize(rawName));
        plugin.data().markDirty();
    }

    public boolean deleteArea(Player player, String rawName) {
        String id = sanitize(rawName);
        if (DEFAULT_AREA.equals(id)) return false;
        PlayerData data = plugin.data().player(player.getUniqueId(), player.getName());
        boolean removed = data.areas().remove(id);
        if (id.equals(data.activeAreaId())) data.activeAreaId(DEFAULT_AREA);
        for (MachineData machine : plugin.data().allMachines()) {
            if (machine.owner().equals(player.getUniqueId()) && id.equals(machine.areaId())) {
                machine.areaId(DEFAULT_AREA);
                removed = true;
            }
        }
        if (removed) plugin.data().markDirty();
        return removed;
    }

    public String activeArea(Player player) {
        PlayerData data = plugin.data().player(player.getUniqueId(), player.getName());
        String id = sanitize(data.activeAreaId());
        data.areas().add(id);
        return id;
    }

    public void activeArea(Player player, String rawArea) {
        PlayerData data = plugin.data().player(player.getUniqueId(), player.getName());
        String id = sanitize(rawArea);
        data.areas().add(id);
        data.activeAreaId(id);
        plugin.data().markDirty();
    }

    public List<String> areasFor(UUID owner) {
        Set<String> ids = new LinkedHashSet<>();
        ids.add(DEFAULT_AREA);
        PlayerData data = plugin.data().player(owner);
        if (data != null) ids.addAll(data.areas());
        for (MachineData machine : plugin.data().allMachines()) {
            if (machine.owner().equals(owner)) ids.add(sanitize(machine.areaId()));
        }
        List<String> sorted = new ArrayList<>(ids);
        sorted.sort(Comparator.comparing(this::displayName));
        if (sorted.remove(DEFAULT_AREA)) sorted.add(0, DEFAULT_AREA);
        return sorted;
    }

    public int machineCount(UUID owner, String rawArea) {
        String id = sanitize(rawArea);
        int count = 0;
        for (MachineData machine : plugin.data().allMachines()) {
            if (machine.owner().equals(owner) && id.equals(machine.areaId())) count++;
        }
        return count;
    }

    public boolean ownsArea(UUID owner, String rawArea) {
        String id = sanitize(rawArea);
        if (DEFAULT_AREA.equals(id)) return true;
        PlayerData data = plugin.data().player(owner);
        if (data != null && data.areas().contains(id)) return true;
        for (MachineData machine : plugin.data().allMachines()) {
            if (machine.owner().equals(owner) && id.equals(machine.areaId())) return true;
        }
        return false;
    }

    public void assign(MachineData machine, String rawArea) {
        machine.areaId(sanitize(rawArea));
        plugin.data().markDirty();
    }
}
