package com.weston.megastocks.machine;

import com.weston.megastocks.area.AreaManager;
import com.weston.megastocks.item.HardwareCategory;
import com.weston.megastocks.item.HardwareKind;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class MachineData {
    private final String locationKey;
    private final UUID owner;
    private String ownerName;
    private MachineKind kind;
    private boolean enabled;
    private double heat;
    private double storedBtc;
    private int level;
    private long placedAt;
    private String areaId;
    private final Map<HardwareCategory, List<HardwareKind>> installed = new EnumMap<>(HardwareCategory.class);

    public MachineData(String locationKey, UUID owner, String ownerName, MachineKind kind) {
        this.locationKey = locationKey;
        this.owner = owner;
        this.ownerName = ownerName;
        this.kind = kind;
        this.enabled = true;
        this.heat = 0.0;
        this.storedBtc = 0.0;
        this.level = 1;
        this.placedAt = System.currentTimeMillis();
        this.areaId = AreaManager.DEFAULT_AREA;
    }

    public String locationKey() { return locationKey; }
    public UUID owner() { return owner; }
    public String ownerName() { return ownerName; }
    public void ownerName(String ownerName) { this.ownerName = ownerName; }
    public MachineKind kind() { return kind; }
    public void kind(MachineKind kind) { this.kind = kind; }
    public boolean enabled() { return enabled; }
    public void enabled(boolean enabled) { this.enabled = enabled; }
    public double heat() { return heat; }
    public void heat(double heat) { this.heat = Math.max(0.0, heat); }
    public double storedBtc() { return storedBtc; }
    public void storedBtc(double storedBtc) { this.storedBtc = Math.max(0.0, storedBtc); }
    public int level() { return level; }
    public void level(int level) { this.level = Math.max(1, level); }
    public long placedAt() { return placedAt; }
    public void placedAt(long placedAt) { this.placedAt = placedAt; }
    public String areaId() { return areaId == null || areaId.isBlank() ? AreaManager.DEFAULT_AREA : areaId; }
    public void areaId(String areaId) { this.areaId = areaId == null || areaId.isBlank() ? AreaManager.DEFAULT_AREA : areaId; }

    public Map<HardwareCategory, List<HardwareKind>> installedHardware() { return installed; }
    public List<HardwareKind> installed(HardwareCategory category) { return installed.computeIfAbsent(category, c -> new ArrayList<>()); }
    public void loadInstalled(HardwareCategory category, List<HardwareKind> parts) { installed.put(category, new ArrayList<>(parts)); }

    public boolean supportsHardware() { return kind.isMiner() || kind.category() == MachineCategory.STORAGE; }
    public boolean isInstallableCategory(HardwareCategory category) {
        return switch (category) {
            case GPU, CPU, RAM, MOTHERBOARD, PSU, STORAGE, NETWORK, MODULE, COOLING_PART -> true;
            case MATERIAL, TOOL, CURRENCY -> false;
        };
    }
    public boolean canInstall(HardwareKind hardware) {
        if (hardware == null || !supportsHardware() || !isInstallableCategory(hardware.category())) return false;
        return installed(hardware.category()).size() < maxSlots(hardware.category());
    }
    public boolean install(HardwareKind hardware) { if (!canInstall(hardware)) return false; installed(hardware.category()).add(hardware); return true; }
    public HardwareKind removeLast(HardwareCategory category) {
        List<HardwareKind> list = installed(category);
        if (list.isEmpty()) return null;
        return list.remove(list.size() - 1);
    }
    public int installedCount() { int c = 0; for (List<HardwareKind> l : installed.values()) c += l.size(); return c; }
    public int installedCount(HardwareCategory category) { return installed(category).size(); }
    public boolean hasInstalled(HardwareKind kind) {
        if (kind == null) return false;
        for (List<HardwareKind> list : installed.values()) if (list.contains(kind)) return true;
        return false;
    }

    public int maxSlots(HardwareCategory category) {
        if (!supportsHardware() || !isInstallableCategory(category)) return 0;
        boolean rack = kind == MachineKind.SERVER_RACK_BASIC || kind == MachineKind.SERVER_RACK_ADVANCED || kind == MachineKind.QUANTUM_SERVER_RACK || kind == MachineKind.AI_SERVER_CLUSTER;
        boolean vault = kind == MachineKind.BANK_VAULT;
        boolean huge = switch (kind) { case MEGA_HASH_RACK, INDUSTRIAL_MINING_UNIT, QUANTUM_MINER, GALAXY_MINER, BLACK_HOLE_MINER, VOID_MINER -> true; default -> false; };
        return switch (category) {
            case GPU -> {
                if (kind == MachineKind.CARDBOARD_MINER || kind == MachineKind.BASIC_MINER) yield 1;
                if (kind == MachineKind.DUAL_GPU_RIG) yield 2;
                if (kind == MachineKind.QUAD_GPU_RIG) yield 4;
                if (kind == MachineKind.ADVANCED_GPU_RIG) yield 6;
                if (kind == MachineKind.ASIC_MINER || kind == MachineKind.WATER_COOLED_ASIC) yield 1;
                if (rack) yield 6;
                if (huge) yield 8;
                yield 2;
            }
            case CPU -> rack || huge || vault ? 2 : 1;
            case RAM -> rack || huge ? 6 : vault ? 4 : 2;
            case MOTHERBOARD -> 1;
            case PSU -> rack || huge ? 2 : 1;
            case STORAGE -> rack || huge || vault ? 4 : 1;
            case NETWORK -> rack || huge ? 2 : vault ? 4 : 1;
            case MODULE -> rack || huge ? 4 : vault ? 3 : 2;
            case COOLING_PART -> rack || huge || vault ? 4 : 2;
            case MATERIAL, TOOL, CURRENCY -> 0;
        };
    }

    public double levelMultiplier() { return 1.0 + ((level - 1) * 0.18); }
    public double hardwarePerformance() {
        double total = 0.0;
        for (List<HardwareKind> list : installed.values()) for (HardwareKind h : list) {
            switch (h.category()) {
                case GPU -> total += h.performance();
                case CPU -> total += h.performance() * 0.55;
                case RAM -> total += h.performance() * 0.18;
                case STORAGE -> total += h.performance() * 0.10;
                case NETWORK -> total += h.performance() * 0.16;
                case MODULE -> total += h.performance() * 0.35;
                case MOTHERBOARD -> total += h.performance() * 0.08;
                default -> { }
            }
        }
        return total;
    }
    public double hardwarePowerUse() {
        double total = 0.0;
        for (List<HardwareKind> list : installed.values()) for (HardwareKind h : list) {
            if (h.category() == HardwareCategory.PSU) continue;
            if (h.watts() > 0) total += h.watts();
        }
        return total;
    }
    public double hardwareHeat() {
        double total = 0.0;
        for (List<HardwareKind> list : installed.values()) for (HardwareKind h : list) total += h.heat();
        return total;
    }
    public double hardwareEfficiencyMultiplier() {
        double multiplier = 1.0 + (installedCount(HardwareCategory.PSU) * 0.03) + (installedCount(HardwareCategory.MOTHERBOARD) * 0.04) + (installedCount(HardwareCategory.NETWORK) * 0.02);
        if (hasInstalled(HardwareKind.OVERCLOCK_MODULE)) multiplier += 0.18;
        if (hasInstalled(HardwareKind.EXTREME_OVERCLOCK_MODULE)) multiplier += 0.45;
        if (hasInstalled(HardwareKind.QUANTUM_HASH_MODULE)) multiplier += 0.70;
        if (hasInstalled(HardwareKind.THERMAL_OPTIMIZER)) multiplier += 0.08;
        return Math.min(2.50, multiplier);
    }
    public double effectiveBtcPerMinute() {
        if (!kind.isMiner()) return 0.0;
        return ((kind.btcPerMinute() * levelMultiplier()) + (hardwarePerformance() * 0.000000018)) * hardwareEfficiencyMultiplier();
    }
    public double effectiveHashRate() { return (kind.hashRate() * levelMultiplier()) + hardwarePerformance(); }
    public double effectivePowerUse() {
        double power = kind.powerUse() * (1.0 + ((level - 1) * 0.10)) + hardwarePowerUse();
        int psus = installedCount(HardwareCategory.PSU);
        if (psus > 0) power *= Math.max(0.75, 1.0 - (psus * 0.04));
        return Math.max(0.0, power);
    }
    public double effectiveHeat() { return (kind.heatPerTick() * (1.0 + ((level - 1) * 0.08))) + hardwareHeat(); }
}
