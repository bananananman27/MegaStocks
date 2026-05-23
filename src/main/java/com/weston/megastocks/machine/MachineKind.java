package com.weston.megastocks.machine;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public enum MachineKind {
    CARDBOARD_MINER("cardboard_miner", "Cardboard Miner", MachineCategory.MINER, Material.BARREL, 75, 18, 0, 4, 6, 0.0000015),
    BASIC_MINER("basic_miner", "Basic Miner", MachineCategory.MINER, Material.FURNACE, 250, 75, 0, 16, 18, 0.0000100),
    DUAL_GPU_RIG("dual_gpu_rig", "Dual GPU Rig", MachineCategory.MINER, Material.SMOKER, 750, 180, 0, 34, 42, 0.0000320),
    QUAD_GPU_RIG("quad_gpu_rig", "Quad GPU Rig", MachineCategory.MINER, Material.BLAST_FURNACE, 1800, 410, 0, 78, 96, 0.0000810),
    ADVANCED_GPU_RIG("advanced_gpu_rig", "Advanced GPU Rig", MachineCategory.MINER, Material.OBSERVER, 4200, 850, 0, 130, 190, 0.0001750),
    ASIC_MINER("asic_miner", "ASIC Miner", MachineCategory.MINER, Material.LODESTONE, 9500, 1300, 0, 220, 410, 0.0004100),
    WATER_COOLED_ASIC("water_cooled_asic", "Water-Cooled ASIC", MachineCategory.MINER, Material.COPPER_BLOCK, 17500, 1750, 0, 150, 680, 0.0007100),
    SERVER_RACK_MINER("server_rack_miner", "Server Rack Miner", MachineCategory.MINER, Material.DEEPSLATE_TILES, 30000, 2550, 0, 315, 1220, 0.0012500),
    MEGA_HASH_RACK("mega_hash_rack", "Mega Hash Rack", MachineCategory.MINER, Material.RESPAWN_ANCHOR, 65000, 4100, 0, 640, 2600, 0.0028000),
    INDUSTRIAL_MINING_UNIT("industrial_mining_unit", "Industrial Mining Unit", MachineCategory.MINER, Material.POLISHED_BLACKSTONE_BRICKS, 125000, 7000, 0, 1200, 5200, 0.0060000),
    ENDER_SINGULARITY_MINER("ender_singularity_miner", "Ender Singularity Miner", MachineCategory.MINER, Material.END_STONE_BRICKS, 185000, 8300, 0, 1420, 8200, 0.0095000),
    BEACON_POWERED_MINER("beacon_powered_miner", "Beacon-Powered Miner", MachineCategory.MINER, Material.SEA_LANTERN, 220000, 9000, 0, 1550, 9800, 0.0120000),
    QUANTUM_MINER("quantum_miner", "Quantum Miner", MachineCategory.MINER, Material.PURPUR_BLOCK, 250000, 9500, 0, 1700, 11000, 0.0140000),
    VOID_MINER("void_miner", "Void Miner", MachineCategory.MINER, Material.SCULK_CATALYST, 420000, 14500, 0, 3300, 24000, 0.0320000),
    GALAXY_MINER("galaxy_miner", "Galaxy Miner", MachineCategory.MINER, Material.AMETHYST_BLOCK, 650000, 18500, 0, 4800, 36000, 0.0520000),
    BLACK_HOLE_MINER("black_hole_miner", "Black Hole Miner", MachineCategory.MINER, Material.SCULK_SHRIEKER, 1000000, 25000, 0, 7000, 55000, 0.0800000),

    HAND_CRANK_GENERATOR("hand_crank_generator", "Hand-Crank Generator", MachineCategory.GENERATOR, Material.CRAFTING_TABLE, 100, 0, 90, 0, 0, 0),
    COAL_GENERATOR("coal_generator", "Coal Generator", MachineCategory.GENERATOR, Material.FURNACE, 500, 0, 450, 0, 0, 0),
    LAVA_GENERATOR("lava_generator", "Lava Generator", MachineCategory.GENERATOR, Material.MAGMA_BLOCK, 1800, 0, 1250, 0, 0, 0),
    SOLAR_ARRAY("solar_array", "Solar Array", MachineCategory.GENERATOR, Material.DAYLIGHT_DETECTOR, 3000, 0, 1600, 0, 0, 0),
    WIND_TURBINE_SMALL("wind_turbine_small", "Small Wind Turbine", MachineCategory.GENERATOR, Material.LIGHTNING_ROD, 4200, 0, 2200, 0, 0, 0),
    BATTERY_BANK("battery_bank", "Battery Bank", MachineCategory.GENERATOR, Material.RED_CONCRETE, 6800, 0, 3100, 0, 0, 0),
    LAVA_BATTERY_BANK("lava_battery_bank", "Lava Battery Bank", MachineCategory.GENERATOR, Material.CUT_COPPER, 14000, 0, 7200, 0, 0, 0),
    REACTOR_GENERATOR("reactor_generator", "Reactor Generator", MachineCategory.GENERATOR, Material.CRYING_OBSIDIAN, 35000, 0, 14000, 0, 0, 0),
    NETHER_REACTOR("nether_reactor", "Nether Reactor", MachineCategory.GENERATOR, Material.NETHER_GOLD_ORE, 70000, 0, 27000, 0, 0, 0),
    ENDER_REACTOR("ender_reactor", "Ender Reactor", MachineCategory.GENERATOR, Material.PURPUR_PILLAR, 115000, 0, 42000, 0, 0, 0),
    QUANTUM_POWER_CORE("quantum_power_core", "Quantum Power Core", MachineCategory.GENERATOR, Material.SEA_LANTERN, 275000, 0, 100000, 0, 0, 0),
    STELLAR_POWER_CORE("stellar_power_core", "Stellar Power Core", MachineCategory.GENERATOR, Material.SEA_LANTERN, 500000, 0, 185000, 0, 0, 0),

    POWER_WIRE("power_wire", "Power Wire", MachineCategory.WIRE, Material.RED_CONCRETE, 35, 0, 0, 0, 0, 0),
    HEAVY_POWER_WIRE("heavy_power_wire", "Heavy Power Wire", MachineCategory.WIRE, Material.COPPER_BLOCK, 120, 0, 0, 0, 0, 0),
    HIGH_VOLTAGE_CABLE("high_voltage_cable", "High Voltage Cable", MachineCategory.WIRE, Material.LIGHTNING_ROD, 450, 0, 0, 0, 0, 0),
    AREA_RELAY_NODE("area_relay_node", "Area Relay Node", MachineCategory.WIRE, Material.TARGET, 1100, 8, 0, 0, 0, 0),
    QUANTUM_LINK_CABLE("quantum_link_cable", "Quantum Link Cable", MachineCategory.WIRE, Material.END_ROD, 7500, 25, 0, 0, 0, 0),
    COOLANT_PIPE("coolant_pipe", "Coolant Pipe", MachineCategory.WIRE, Material.PRISMARINE_BRICKS, 55, 4, 0, -35, 0, 0),
    CRYO_COOLANT_PIPE("cryo_coolant_pipe", "Cryo Coolant Pipe", MachineCategory.WIRE, Material.BLUE_ICE, 380, 12, 0, -210, 0, 0),
    UNIVERSAL_BUS_CABLE("universal_bus_cable", "Universal Bus Cable", MachineCategory.WIRE, Material.CHAIN, 850, 10, 0, -90, 0, 0),

    SMALL_COOLING_FAN("small_cooling_fan", "Small Cooling Fan", MachineCategory.COOLING, Material.IRON_TRAPDOOR, 200, 35, 0, -45, 0, 0),
    INDUSTRIAL_FAN("industrial_fan", "Industrial Fan", MachineCategory.COOLING, Material.CHAIN, 1200, 95, 0, -140, 0, 0),
    LIQUID_COOLING_TANK("liquid_cooling_tank", "Liquid Cooling Tank", MachineCategory.COOLING, Material.CAULDRON, 3500, 180, 0, -360, 0, 0),
    IMMERSION_COOLING_BATH("immersion_cooling_bath", "Immersion Cooling Bath", MachineCategory.COOLING, Material.CAULDRON, 6200, 260, 0, -650, 0, 0),
    COOLING_TOWER("cooling_tower", "Cooling Tower", MachineCategory.COOLING, Material.PRISMARINE, 8500, 310, 0, -900, 0, 0),
    FREEZER_UNIT("freezer_unit", "Freezer Unit", MachineCategory.COOLING, Material.PACKED_ICE, 14500, 430, 0, -1550, 0, 0),
    CRYO_CHAMBER("cryo_chamber", "Cryo Chamber", MachineCategory.COOLING, Material.BLUE_ICE, 24000, 625, 0, -2500, 0, 0),
    VOID_COOLER("void_cooler", "Void Cooler", MachineCategory.COOLING, Material.END_STONE_BRICKS, 90000, 1200, 0, -9000, 0, 0),
    ABSOLUTE_ZERO_CORE("absolute_zero_core", "Absolute Zero Core", MachineCategory.COOLING, Material.BLUE_ICE, 180000, 2200, 0, -21000, 0, 0),

    SERVER_COOLING_BLOCK("server_cooling_block", "Server Cooling Block", MachineCategory.COOLING, Material.PACKED_ICE, 65000, 700, 0, -12000, 0, 0),
    CRYO_SERVER_BLOCK("cryo_server_block", "Cryo Server Block", MachineCategory.COOLING, Material.BLUE_ICE, 145000, 1200, 0, -28000, 0, 0),
    LIQUID_NITROGEN_BLOCK("liquid_nitrogen_block", "Liquid Nitrogen Block", MachineCategory.COOLING, Material.SNOW_BLOCK, 240000, 1800, 0, -52000, 0, 0),
    AREA_FROST_BEACON("area_frost_beacon", "Area Frost Beacon", MachineCategory.COOLING, Material.SEA_LANTERN, 475000, 2600, 0, -115000, 0, 0),

    RESOURCE_MONITOR_STATION("resource_monitor_station", "Resource Monitor Station", MachineCategory.STATION, Material.LECTERN, 800, 15, 0, 0, 0, 0),
    HARDWARE_BENCH("hardware_bench", "Hardware Bench", MachineCategory.STATION, Material.SMITHING_TABLE, 650, 10, 0, 0, 0, 0),
    MINING_CONTROL_STATION("mining_control_station", "Mining Control Station", MachineCategory.STATION, Material.CARTOGRAPHY_TABLE, 1200, 20, 0, 0, 0, 0),
    POWER_MANAGEMENT_STATION("power_management_station", "Power Management Station", MachineCategory.STATION, Material.COMPARATOR, 1000, 12, 0, 0, 0, 0),
    STOCK_TRADING_STATION("stock_trading_station", "Stock Trading Station", MachineCategory.STATION, Material.ENCHANTING_TABLE, 1400, 20, 0, 0, 0, 0),
    UPGRADE_STATION("upgrade_station", "Upgrade Station", MachineCategory.STATION, Material.ANVIL, 1750, 18, 0, 0, 0, 0),
    REPAIR_STATION("repair_station", "Repair Station", MachineCategory.STATION, Material.GRINDSTONE, 950, 10, 0, 0, 0, 0),
    ATM_STATION("atm_station", "ATM Station", MachineCategory.STATION, Material.YELLOW_CONCRETE, 550, 8, 0, 0, 0, 0),
    SELL_STATION("sell_station", "Sell Station", MachineCategory.STATION, Material.LIME_CONCRETE, 900, 10, 0, 0, 0, 0),
    BLACK_MARKET_STATION("black_market_station", "Black Market Station", MachineCategory.STATION, Material.BLACKSTONE, 8500, 40, 0, 0, 0, 0),
    AREA_CONTROL_TERMINAL("area_control_terminal", "Area Control Terminal", MachineCategory.STATION, Material.CARTOGRAPHY_TABLE, 2200, 18, 0, 0, 0, 0),
    AREA_MONITOR_STATION("area_monitor_station", "Area Monitor Station", MachineCategory.STATION, Material.LODESTONE, 2800, 18, 0, 0, 0, 0),
    NETWORK_OPERATIONS_CENTER("network_operations_center", "Network Operations Center", MachineCategory.STATION, Material.TARGET, 5000, 50, 0, 0, 0, 0),

    BANK_VAULT("bank_vault", "Bank Vault", MachineCategory.STORAGE, Material.POLISHED_DEEPSLATE, 9000, 0, 0, 0, 0, 0),
    SERVER_RACK_BASIC("server_rack_basic", "Basic Server Rack", MachineCategory.STORAGE, Material.BLACKSTONE, 3200, 125, 0, 30, 0, 0),
    SERVER_RACK_ADVANCED("server_rack_advanced", "Advanced Server Rack", MachineCategory.STORAGE, Material.POLISHED_BLACKSTONE_BRICKS, 9000, 310, 0, 65, 0, 0),
    QUANTUM_SERVER_RACK("quantum_server_rack", "Quantum Server Rack", MachineCategory.STORAGE, Material.PURPUR_PILLAR, 45000, 780, 0, 120, 0, 0),
    AI_SERVER_CLUSTER("ai_server_cluster", "AI Server Cluster", MachineCategory.STORAGE, Material.CHISELED_BOOKSHELF, 75000, 1250, 0, 230, 0, 0),
    HOLOGRAM_DATA_DISPLAY("hologram_data_display", "Hologram Data Display", MachineCategory.DECORATION, Material.SEA_LANTERN, 2500, 45, 0, 0, 0, 0),
    BULL_MARKET_BEACON("bull_market_beacon", "Bull Market Beacon", MachineCategory.DECORATION, Material.LIME_CONCRETE, 12500, 55, 0, 0, 0, 0),
    CRASH_ALARM("crash_alarm", "Market Crash Alarm", MachineCategory.DECORATION, Material.REDSTONE_LAMP, 3500, 35, 0, 0, 0, 0);

    private final String id;
    private final String displayName;
    private final MachineCategory category;
    private final Material blockMaterial;
    private final double cost;
    private final double powerUse;
    private final double powerProduction;
    private final double heatPerTick;
    private final double hashRate;
    private final double btcPerMinute;

    MachineKind(String id, String displayName, MachineCategory category, Material blockMaterial, double cost, double powerUse, double powerProduction, double heatPerTick, double hashRate, double btcPerMinute) {
        this.id = id;
        this.displayName = displayName;
        this.category = category;
        this.blockMaterial = blockMaterial;
        this.cost = cost;
        this.powerUse = powerUse;
        this.powerProduction = powerProduction;
        this.heatPerTick = heatPerTick;
        this.hashRate = hashRate;
        this.btcPerMinute = btcPerMinute;
    }

    public String id() { return id; }
    public String displayName() { return displayName; }
    public MachineCategory category() { return category; }
    public Material blockMaterial() { return blockMaterial; }
    public double cost() { return cost; }
    public double powerUse() { return powerUse; }
    public double powerProduction() { return powerProduction; }
    public double heatPerTick() { return heatPerTick; }
    public double hashRate() { return hashRate; }
    public double btcPerMinute() { return btcPerMinute; }
    public boolean isMiner() { return category == MachineCategory.MINER; }
    public boolean isGenerator() { return category == MachineCategory.GENERATOR; }
    public boolean isCooler() { return category == MachineCategory.COOLING || (category == MachineCategory.WIRE && (id.contains("coolant") || id.contains("cryo") || id.contains("bus"))); }
    public boolean isWire() { return category == MachineCategory.WIRE; }
    public boolean isCoolingWire() { return isWire() && (id.contains("coolant") || id.contains("cryo") || id.contains("bus")); }
    public boolean isServerCooler() { return switch (this) {
        case SERVER_COOLING_BLOCK, CRYO_SERVER_BLOCK, LIQUID_NITROGEN_BLOCK, AREA_FROST_BEACON -> true;
        default -> false;
    }; }
    public boolean isStation() { return category == MachineCategory.STATION; }

    public static MachineKind byId(String id) {
        if (id == null) return null;
        for (MachineKind kind : values()) if (kind.id.equalsIgnoreCase(id) || kind.name().equalsIgnoreCase(id)) return kind;
        return null;
    }

    public static List<MachineKind> byCategory(MachineCategory category) {
        return Arrays.stream(values()).filter(k -> k.category == category).toList();
    }
}
