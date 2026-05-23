package com.weston.megastocks.machine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class NetworkStats {
    public int totalMachines;
    public int miners;
    public int generators;
    public int coolers;
    public int stations;
    public int wires;
    public int serverCoolingBlocks;
    public int overheated;
    public int hardwareInstalled;
    public double powerProduced;
    public double powerUsed;
    public double coolingProduced;
    public double heatProduced;
    public double btcPerMinute;
    public double storedBtc;
    public double totalHashRate;
    public final List<MachineData> topPowerUsers = new ArrayList<>();
    public final List<MachineData> topHeatMakers = new ArrayList<>();
    public final List<MachineData> topEarners = new ArrayList<>();

    public double wirePowerMultiplier() {
        return 1.0 + Math.min(0.40, wires * 0.025);
    }

    public double wireCoolingMultiplier() {
        return 1.0 + Math.min(0.90, wires * 0.035 + serverCoolingBlocks * 0.03);
    }

    public double effectivePowerProduced() {
        return powerProduced * wirePowerMultiplier();
    }

    public double effectiveCoolingProduced() {
        return coolingProduced * wireCoolingMultiplier();
    }

    public double freePower() {
        return effectivePowerProduced() - powerUsed;
    }

    public double netCooling() {
        return effectiveCoolingProduced() - heatProduced;
    }

    public boolean powerDeficit() {
        return powerUsed > effectivePowerProduced() && powerUsed > 0.0;
    }

    public boolean coolingDeficit() {
        return heatProduced > effectiveCoolingProduced() && heatProduced > 0.0;
    }

    public void sortTopLists() {
        topPowerUsers.sort(Comparator.comparingDouble(MachineData::effectivePowerUse).reversed());
        topHeatMakers.sort(Comparator.comparingDouble(MachineData::effectiveHeat).reversed());
        topEarners.sort(Comparator.comparingDouble(MachineData::effectiveBtcPerMinute).reversed());
        trim(topPowerUsers);
        trim(topHeatMakers);
        trim(topEarners);
    }

    private void trim(List<MachineData> list) {
        while (list.size() > 10) list.remove(list.size() - 1);
    }
}
