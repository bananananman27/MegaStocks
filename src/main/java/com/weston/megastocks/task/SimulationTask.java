package com.weston.megastocks.task;

import com.weston.megastocks.MegaStocksPlugin;
import com.weston.megastocks.data.PlayerData;
import com.weston.megastocks.item.HardwareKind;
import com.weston.megastocks.machine.MachineData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.weston.megastocks.machine.NetworkStats;

import java.util.Map;

public final class SimulationTask implements Runnable {
    private final MegaStocksPlugin plugin;

    public SimulationTask(MegaStocksPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        int interval = Math.max(2, plugin.getConfig().getInt("performance.simulation-interval-seconds", 10));
        double minutes = interval / 60.0;
        double overheatAt = plugin.getConfig().getDouble("machine.overheat-at", 100.0);
        double passiveCooldown = plugin.getConfig().getDouble("machine.passive-cooldown-per-tick", 2.0);
        double heatScale = plugin.getConfig().getDouble("machine.heat-scale", 1.0);
        double powerDeficitMultiplier = plugin.getConfig().getDouble("machine.power-deficit-production-multiplier", 0.25);

        Map<String, NetworkStats> statsMap = plugin.machines().allOwnerAreaStats();
        boolean changed = false;

        for (MachineData data : plugin.data().allMachines()) {
            NetworkStats stats = statsMap.get(plugin.machines().areaStatsKey(data.owner(), data.areaId()));
            if (stats == null) continue;

            if (!data.enabled()) {
                if (data.heat() > 0) {
                    data.heat(Math.max(0, data.heat() - passiveCooldown));
                    changed = true;
                }
                continue;
            }

            double productionMultiplier = 1.0;
            if (stats.powerDeficit()) productionMultiplier *= powerDeficitMultiplier;
            if (data.heat() >= overheatAt) productionMultiplier = 0.0;

            if (data.kind().isMiner() && productionMultiplier > 0) {
                double generated = data.effectiveBtcPerMinute() * minutes * productionMultiplier;
                if (generated > 0) {
                    data.storedBtc(data.storedBtc() + generated);
                    if (data.hasInstalled(HardwareKind.AUTO_COLLECT_MODULE) || data.hasInstalled(HardwareKind.AUTO_SELL_MODULE)) {
                        PlayerData ownerData = plugin.data().player(data.owner());
                        if (ownerData != null && data.storedBtc() > 0) {
                            ownerData.fakeBtc(ownerData.fakeBtc() + data.storedBtc());
                            data.storedBtc(0.0);
                        }
                    }
                    if (data.hasInstalled(HardwareKind.AUTO_SELL_MODULE)) {
                        Player onlineOwner = Bukkit.getPlayer(data.owner());
                        PlayerData ownerData = plugin.data().player(data.owner());
                        if (onlineOwner != null && ownerData != null && ownerData.fakeBtc() > 0.0 && plugin.economy().isReady()) {
                            double dollars = ownerData.fakeBtc() * plugin.machines().currentBtcSellPrice();
                            if (plugin.economy().deposit(onlineOwner, dollars)) ownerData.fakeBtc(0.0);
                        }
                    }
                    changed = true;
                }
            }

            double heatChange = heatChangeFor(data, stats, heatScale, minutes, passiveCooldown);
            if (heatChange != 0.0) {
                double before = data.heat();
                data.heat(Math.max(0.0, data.heat() + heatChange));
                if (data.heat() >= overheatAt) data.enabled(false);
                if (Math.abs(before - data.heat()) > 0.000001) changed = true;
            }
        }

        if (changed) plugin.data().markDirty();
    }

    private double heatChangeFor(MachineData data, NetworkStats stats, double heatScale, double minutes, double passiveCooldown) {
        double machineHeat = data.effectiveHeat();
        if (machineHeat > 0) {
            double generatedHeat = (machineHeat * heatScale * minutes) / 10.0;
            double effectiveCooling = stats.effectiveCoolingProduced();
            if (effectiveCooling <= 0.0 || stats.heatProduced <= 0.0) return generatedHeat;

            double share = machineHeat / Math.max(stats.heatProduced, machineHeat);
            double coolingApplied = (effectiveCooling * heatScale * minutes / 10.0) * share;
            return generatedHeat - coolingApplied;
        }

        if (machineHeat < 0 || stats.netCooling() > 0) return -passiveCooldown;
        return 0.0;
    }
}
