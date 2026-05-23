package com.weston.megastocks.machine;

import com.weston.megastocks.MegaStocksPlugin;
import com.weston.megastocks.data.PlayerData;
import com.weston.megastocks.item.HardwareKind;
import com.weston.megastocks.util.LocationKey;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class MachineManager {
    private final MegaStocksPlugin plugin;

    public MachineManager(MegaStocksPlugin plugin) {
        this.plugin = plugin;
    }

    public MachineData at(Location location) {
        return plugin.data().machine(LocationKey.of(location));
    }

    public MachineData at(Block block) {
        return at(block.getLocation());
    }

    public int countOwned(UUID owner) {
        int count = 0;
        for (MachineData data : plugin.data().allMachines()) {
            if (data.owner().equals(owner)) count++;
        }
        return count;
    }

    public boolean canPlace(Player player) {
        int serverMax = plugin.getConfig().getInt("performance.max-machines-server", 10000);
        int playerMax = plugin.getConfig().getInt("performance.max-machines-per-player", 250);
        if (plugin.data().machines().size() >= serverMax && !player.hasPermission("megastocks.admin")) return false;
        return countOwned(player.getUniqueId()) < playerMax || player.hasPermission("megastocks.admin");
    }

    public MachineData register(Location location, Player owner, MachineKind kind) {
        String key = LocationKey.of(location);
        MachineData data = new MachineData(key, owner.getUniqueId(), owner.getName(), kind);
        data.areaId(plugin.areas().activeArea(owner));
        plugin.data().putMachine(data);
        return data;
    }

    public void unregister(Location location) {
        plugin.data().removeMachine(LocationKey.of(location));
    }


    public boolean canPickupOrSteal(Player player, MachineData data, boolean sendMessage) {
        boolean owner = data.owner().equals(player.getUniqueId());
        boolean adminBreak = player.hasPermission("megastocks.break.other") || player.hasPermission("megastocks.admin");
        boolean stealingEnabled = plugin.getConfig().getBoolean("machine.allow-player-stealing", true);
        if (owner || adminBreak || stealingEnabled) return true;
        if (sendMessage) plugin.msg().send(player, "&cThat machine belongs to &e" + data.ownerName() + "&c. Machine stealing is disabled by the server.");
        return false;
    }

    public boolean pickupMachine(Player player, MachineData data, boolean notifySteal) {
        if (data == null || !canPickupOrSteal(player, data, true)) return false;
        boolean owner = data.owner().equals(player.getUniqueId());
        boolean adminBreak = player.hasPermission("megastocks.break.other") || player.hasPermission("megastocks.admin");
        boolean thief = !owner && !adminBreak;

        Location location = LocationKey.parse(data.locationKey());
        if (location == null || location.getWorld() == null) {
            plugin.data().removeMachine(data.locationKey());
            plugin.data().markDirty();
            plugin.msg().send(player, "&cThat machine location could not be found, so it was removed from saved data.");
            return false;
        }

        double stored = data.storedBtc();
        if (stored > 0) {
            PlayerData pd = plugin.data().player(player.getUniqueId(), player.getName());
            pd.fakeBtc(pd.fakeBtc() + stored);
            plugin.msg().send(player, "&aRecovered &b" + plugin.msg().btc(stored) + " &afrom that machine.");
        }

        Location dropLocation = location.clone().add(0.5, 0.5, 0.5);
        for (Map.Entry<?, List<HardwareKind>> entry : data.installedHardware().entrySet()) {
            for (HardwareKind h : entry.getValue()) {
                HashMap<Integer, org.bukkit.inventory.ItemStack> leftovers = player.getInventory().addItem(plugin.items().hardwareItem(h, 1));
                leftovers.values().forEach(item -> location.getWorld().dropItemNaturally(dropLocation, item));
            }
        }
        HashMap<Integer, org.bukkit.inventory.ItemStack> leftovers = player.getInventory().addItem(plugin.items().machineItem(data.kind(), 1));
        leftovers.values().forEach(item -> location.getWorld().dropItemNaturally(dropLocation, item));

        location.getBlock().setType(Material.AIR);
        plugin.data().removeMachine(data.locationKey());
        plugin.data().markDirty();

        if (thief) {
            plugin.msg().send(player, "&cStole &6" + data.kind().displayName() + " &cfrom &e" + data.ownerName() + "&c and got its installed hardware/BTC.");
            if (notifySteal && plugin.getConfig().getBoolean("machine.notify-owner-on-steal", true)) {
                Player ownerPlayer = plugin.getServer().getPlayer(data.owner());
                if (ownerPlayer != null && ownerPlayer.isOnline()) {
                    plugin.msg().send(ownerPlayer, "&cYour &6" + data.kind().displayName() + " &cat &7" + LocationKey.pretty(data.locationKey()) + " &cwas stolen by &e" + player.getName() + "&c.");
                }
            }
        } else {
            plugin.msg().send(player, "&ePicked up &6" + data.kind().displayName() + "&e from &7" + LocationKey.pretty(data.locationKey()) + "&e.");
        }
        return true;
    }

    public NetworkStats statsFor(UUID owner) {
        return statsFor(owner, null);
    }

    public NetworkStats statsFor(UUID owner, String areaId) {
        NetworkStats stats = new NetworkStats();
        for (MachineData data : plugin.data().allMachines()) {
            if (!data.owner().equals(owner)) continue;
            if (!matchesArea(data, areaId)) continue;
            addToStats(stats, data);
        }
        stats.sortTopLists();
        return stats;
    }

    public Map<UUID, NetworkStats> allOwnerStats() {
        Map<UUID, NetworkStats> map = new HashMap<>();
        for (MachineData data : plugin.data().allMachines()) {
            NetworkStats stats = map.computeIfAbsent(data.owner(), id -> new NetworkStats());
            addToStats(stats, data);
        }
        for (NetworkStats stats : map.values()) stats.sortTopLists();
        return map;
    }

    public Map<String, NetworkStats> allOwnerAreaStats() {
        Map<String, NetworkStats> map = new HashMap<>();
        for (MachineData data : plugin.data().allMachines()) {
            NetworkStats stats = map.computeIfAbsent(areaStatsKey(data.owner(), data.areaId()), id -> new NetworkStats());
            addToStats(stats, data);
        }
        for (NetworkStats stats : map.values()) stats.sortTopLists();
        return map;
    }

    public String areaStatsKey(UUID owner, String areaId) {
        return owner + "|" + plugin.areas().sanitize(areaId);
    }

    private boolean matchesArea(MachineData data, String areaId) {
        if (areaId == null || areaId.isBlank() || "all".equalsIgnoreCase(areaId)) return true;
        return plugin.areas().sanitize(areaId).equals(data.areaId());
    }

    private void addToStats(NetworkStats stats, MachineData data) {
        stats.totalMachines++;
        MachineKind kind = data.kind();
        if (kind.isMiner()) stats.miners++;
        else if (kind.isGenerator()) stats.generators++;
        else if (kind.isCooler()) stats.coolers++;
        else if (kind.isStation()) stats.stations++;
        if (kind.isWire()) stats.wires++;
        if (kind.isServerCooler()) stats.serverCoolingBlocks++;
        if (data.heat() >= plugin.getConfig().getDouble("machine.warning-heat-at", 75.0)) stats.overheated++;
        stats.hardwareInstalled += data.installedCount();

        if (data.enabled()) {
            stats.powerProduced += kind.powerProduction();
            stats.powerUsed += data.effectivePowerUse();
            if (kind.isCooler()) stats.coolingProduced += Math.abs(data.effectiveHeat());
            else if (data.effectiveHeat() > 0) stats.heatProduced += data.effectiveHeat();
            stats.btcPerMinute += data.effectiveBtcPerMinute();
            stats.totalHashRate += data.effectiveHashRate();
        }
        stats.storedBtc += data.storedBtc();
        stats.topPowerUsers.add(data);
        if (data.effectiveHeat() > 0) stats.topHeatMakers.add(data);
        if (data.effectiveBtcPerMinute() > 0) stats.topEarners.add(data);
    }

    public double collectAllBtc(Player player) {
        return collectAllBtc(player, null);
    }

    public double collectAllBtc(Player player, String areaId) {
        double total = 0.0;
        for (MachineData data : plugin.data().allMachines()) {
            if (!data.owner().equals(player.getUniqueId())) continue;
            if (!matchesArea(data, areaId)) continue;
            total += data.storedBtc();
            data.storedBtc(0.0);
        }
        if (total > 0) {
            PlayerData playerData = plugin.data().player(player.getUniqueId(), player.getName());
            playerData.fakeBtc(playerData.fakeBtc() + total);
            plugin.data().markDirty();
        }
        return total;
    }

    public double sellAllBtc(Player player) {
        PlayerData playerData = plugin.data().player(player.getUniqueId(), player.getName());
        double btc = playerData.fakeBtc();
        if (btc <= 0) return 0.0;
        double price = currentBtcSellPrice();
        double dollars = btc * price;
        if (!plugin.economy().deposit(player, dollars)) return -1.0;
        playerData.fakeBtc(0.0);
        plugin.data().markDirty();
        return dollars;
    }

    public double currentBtcSellPrice() {
        double base = plugin.getConfig().getDouble("economy.btc-sell-price", 31500.0);
        return base;
    }

    public void repairNearbyOrOwned(Player player) {
        for (MachineData data : plugin.data().allMachines()) {
            if (!data.owner().equals(player.getUniqueId())) continue;
            data.heat(Math.max(0.0, data.heat() - 35.0));
        }
        plugin.data().markDirty();
    }

    public void toggleMiners(Player player, String areaId) {
        boolean anyEnabled = false;
        for (MachineData data : plugin.data().allMachines()) {
            if (data.owner().equals(player.getUniqueId()) && data.kind().isMiner() && matchesArea(data, areaId) && data.enabled()) {
                anyEnabled = true;
                break;
            }
        }
        for (MachineData data : plugin.data().allMachines()) {
            if (data.owner().equals(player.getUniqueId()) && data.kind().isMiner() && matchesArea(data, areaId)) data.enabled(!anyEnabled);
        }
        plugin.data().markDirty();
    }

    public OfflinePlayer ownerOf(MachineData data) {
        return plugin.getServer().getOfflinePlayer(data.owner());
    }
}
