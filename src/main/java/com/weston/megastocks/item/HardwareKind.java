package com.weston.megastocks.item;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public enum HardwareKind {
    BITCOIN_COIN("bitcoin_coin", "Bitcoin Coin", HardwareCategory.CURRENCY, Material.GOLD_NUGGET, 0, 0, 0, 0, "Fake BTC collectible item"),
    CASH_STACK("cash_stack", "Cash Stack", HardwareCategory.CURRENCY, Material.PAPER, 0, 0, 0, 0, "Decorative cash item"),
    MINING_CHIP("mining_chip", "Mining Chip", HardwareCategory.MATERIAL, Material.ECHO_SHARD, 120, 0, 0, 0, "Upgrade material"),
    ASIC_CHIP("asic_chip", "ASIC Chip", HardwareCategory.MATERIAL, Material.AMETHYST_SHARD, 950, 0, 0, 0, "High-tier miner part"),
    GPU_CARD("gpu_card", "Generic GPU Card", HardwareCategory.GPU, Material.IRON_NUGGET, 300, 35, 70, 12, "Generic GPU component"),
    REPAIR_KIT("repair_kit", "Repair Kit", HardwareCategory.TOOL, Material.HONEYCOMB, 250, 0, 0, -25, "Right-click a machine to repair it"),
    REPAIR_BOX("repair_box", "Repair Box", HardwareCategory.TOOL, Material.CHEST, 600, 0, 0, -65, "Right-click a machine for a real repair"),
    ADVANCED_REPAIR_BOX("advanced_repair_box", "Advanced Repair Box", HardwareCategory.TOOL, Material.TRAPPED_CHEST, 1800, 0, 0, -140, "Strong repair box"),
    EMERGENCY_REPAIR_CRATE("emergency_repair_crate", "Emergency Repair Crate", HardwareCategory.TOOL, Material.BARREL, 4200, 0, 0, -350, "Huge emergency repair crate"),
    WRENCH("wrench", "Machine Wrench", HardwareCategory.TOOL, Material.TRIPWIRE_HOOK, 150, 0, 0, 0, "Utility tool"),
    RISER_CABLE("riser_cable", "PCIe Riser Cable", HardwareCategory.MATERIAL, Material.STRING, 180, 0, 8, 0, "GPU rig material"),
    HASHRATE_ACCELERATOR("hashrate_accelerator", "Hashrate Accelerator", HardwareCategory.MATERIAL, Material.ECHO_SHARD, 22000, 120, 2500, 180, "Rare late-game upgrade material"),

    GPU_OLD_2GB("gpu_old_2gb", "Old 2GB GPU", HardwareCategory.GPU, Material.FLINT, 75, 15, 20, 5, "Dusty starter GPU"),
    GPU_BASIC_4GB("gpu_basic_4gb", "Basic 4GB GPU", HardwareCategory.GPU, Material.IRON_NUGGET, 220, 35, 60, 12, "Cheap entry GPU"),
    GPU_GAMING_8GB("gpu_gaming_8gb", "Gaming 8GB GPU", HardwareCategory.GPU, Material.COPPER_INGOT, 650, 70, 150, 28, "Solid gaming card"),
    GPU_DUAL_FAN_12GB("gpu_dual_fan_12gb", "Dual-Fan 12GB GPU", HardwareCategory.GPU, Material.QUARTZ, 1250, 110, 280, 48, "Mid/high card"),
    GPU_TRIPLE_FAN_16GB("gpu_triple_fan_16gb", "Triple-Fan 16GB GPU", HardwareCategory.GPU, Material.PRISMARINE_CRYSTALS, 2500, 180, 520, 80, "High-end mining card"),
    GPU_CREATOR_24GB("gpu_creator_24gb", "Creator 24GB GPU", HardwareCategory.GPU, Material.LIME_DYE, 5200, 260, 950, 115, "Workstation GPU"),
    GPU_AI_ACCELERATOR("gpu_ai_accelerator", "AI Accelerator GPU", HardwareCategory.GPU, Material.ECHO_SHARD, 12000, 460, 2400, 210, "Datacenter GPU"),
    GPU_LIQUID_COOLED("gpu_liquid_cooled", "Liquid-Cooled GPU", HardwareCategory.GPU, Material.PRISMARINE_CRYSTALS, 20000, 390, 3100, 85, "Efficient high-tier GPU"),
    GPU_QUANTUM_CORE("gpu_quantum_core", "Quantum Core GPU", HardwareCategory.GPU, Material.ECHO_SHARD, 85000, 1100, 15000, 400, "Endgame fictional GPU"),

    CPU_BASIC_DUAL_CORE("cpu_basic_dual_core", "Basic Dual-Core CPU", HardwareCategory.CPU, Material.STONE_BUTTON, 120, 20, 10, 8, "Starter processor"),
    CPU_QUAD_CORE("cpu_quad_core", "Quad-Core CPU", HardwareCategory.CPU, Material.POLISHED_BLACKSTONE_BUTTON, 350, 45, 30, 15, "Basic desktop CPU"),
    CPU_SIX_CORE("cpu_six_core", "Six-Core CPU", HardwareCategory.CPU, Material.REPEATER, 900, 70, 75, 24, "Gaming CPU"),
    CPU_EIGHT_CORE("cpu_eight_core", "Eight-Core CPU", HardwareCategory.CPU, Material.COMPARATOR, 1800, 95, 140, 38, "Strong CPU"),
    CPU_TWELVE_CORE("cpu_twelve_core", "Twelve-Core CPU", HardwareCategory.CPU, Material.HEAVY_WEIGHTED_PRESSURE_PLATE, 3600, 140, 260, 65, "Workstation CPU"),
    CPU_SERVER_CHIP("cpu_server_chip", "Server CPU", HardwareCategory.CPU, Material.LIGHT_WEIGHTED_PRESSURE_PLATE, 8500, 220, 650, 95, "Server-grade CPU"),
    CPU_THREAD_MONSTER("cpu_thread_monster", "Thread Monster CPU", HardwareCategory.CPU, Material.PRISMARINE_SHARD, 18000, 320, 1500, 140, "Huge workstation CPU"),
    CPU_QUANTUM_PROCESSOR("cpu_quantum_processor", "Quantum Processor", HardwareCategory.CPU, Material.PRISMARINE_CRYSTALS, 75000, 900, 9000, 300, "Endgame CPU"),

    RAM_STICK_4GB("ram_stick_4gb", "4GB RAM Stick", HardwareCategory.RAM, Material.GREEN_DYE, 80, 4, 2, 1, "Tiny RAM stick"),
    RAM_STICK_8GB("ram_stick_8gb", "8GB RAM Stick", HardwareCategory.RAM, Material.LIME_DYE, 180, 6, 5, 1, "Starter RAM"),
    RAM_STICK_16GB("ram_stick_16gb", "16GB RAM Stick", HardwareCategory.RAM, Material.CYAN_DYE, 450, 9, 12, 2, "Good RAM"),
    RAM_STICK_32GB_RGB("ram_stick_32gb_rgb", "32GB RGB RAM", HardwareCategory.RAM, Material.MAGENTA_DYE, 1100, 14, 32, 4, "RGB gaming RAM"),
    RAM_STICK_64GB_SERVER("ram_stick_64gb_server", "64GB Server RAM", HardwareCategory.RAM, Material.BLUE_DYE, 2800, 22, 90, 7, "Server RAM"),
    RAM_KIT_128GB("ram_kit_128gb", "128GB RAM Kit", HardwareCategory.RAM, Material.PURPLE_DYE, 7000, 40, 220, 12, "Huge RAM kit"),
    RAM_QUANTUM_MEMORY("ram_quantum_memory", "Quantum Memory", HardwareCategory.RAM, Material.ECHO_SHARD, 50000, 130, 2500, 50, "Endgame RAM"),

    MOTHERBOARD_BASIC("motherboard_basic", "Basic Motherboard", HardwareCategory.MOTHERBOARD, Material.GREEN_CARPET, 250, 0, 0, 0, "Small board"),
    MOTHERBOARD_GAMING("motherboard_gaming", "Gaming Motherboard", HardwareCategory.MOTHERBOARD, Material.BLACK_CARPET, 950, 0, 0, 0, "Gaming board"),
    MOTHERBOARD_MINING_6SLOT("motherboard_mining_6slot", "6-Slot Mining Motherboard", HardwareCategory.MOTHERBOARD, Material.LIGHT_BLUE_CARPET, 2250, 0, 0, 0, "Mining board"),
    MOTHERBOARD_SERVER("motherboard_server", "Server Motherboard", HardwareCategory.MOTHERBOARD, Material.GRAY_CARPET, 7000, 0, 0, 0, "Server board"),

    PSU_400W("psu_400w", "400W PSU", HardwareCategory.PSU, Material.COAL, 180, -400, 0, 8, "Starter PSU"),
    PSU_650W("psu_650w", "650W PSU", HardwareCategory.PSU, Material.CHARCOAL, 400, -650, 0, 14, "Basic PSU"),
    PSU_850W_GOLD("psu_850w_gold", "850W Gold PSU", HardwareCategory.PSU, Material.YELLOW_DYE, 900, -850, 0, 16, "Efficient PSU"),
    PSU_1200W_PLATINUM("psu_1200w_platinum", "1200W Platinum PSU", HardwareCategory.PSU, Material.POLISHED_DEEPSLATE, 2200, -1200, 0, 20, "High-end PSU"),
    PSU_1600W_SERVER("psu_1600w_server", "1600W Server PSU", HardwareCategory.PSU, Material.PRISMARINE_SHARD, 5000, -1600, 0, 28, "Server PSU"),

    HDD_OLD("hdd_old", "Old HDD", HardwareCategory.STORAGE, Material.CLAY_BALL, 70, 5, 1, 1, "Old storage"),
    SSD_SATA("ssd_sata", "SATA SSD", HardwareCategory.STORAGE, Material.BRICK, 300, 4, 10, 1, "Solid storage"),
    NVME_DRIVE("nvme_drive", "NVMe Drive", HardwareCategory.STORAGE, Material.NETHER_BRICK, 850, 6, 32, 2, "Fast storage"),
    RAID_ARRAY("raid_array", "RAID Array", HardwareCategory.STORAGE, Material.IRON_BARS, 3500, 45, 150, 12, "Big storage"),
    QUANTUM_CACHE("quantum_cache", "Quantum Cache", HardwareCategory.STORAGE, Material.SHULKER_SHELL, 48000, 85, 3800, 40, "Endgame storage accelerator"),

    NETWORK_CARD_1G("network_card_1g", "1G Network Card", HardwareCategory.NETWORK, Material.STRING, 120, 3, 8, 1, "Basic network card"),
    NETWORK_CARD_10G("network_card_10g", "10G Network Card", HardwareCategory.NETWORK, Material.REDSTONE_TORCH, 650, 8, 55, 2, "Fast network card"),
    NETWORK_CARD_100G("network_card_100g", "100G Network Card", HardwareCategory.NETWORK, Material.RED_DYE, 4200, 26, 420, 9, "Datacenter network card"),
    AI_TRAINING_MODULE("ai_training_module", "AI Training Module", HardwareCategory.NETWORK, Material.ECHO_SHARD, 38000, 95, 3200, 35, "Boosts smart racks and automation"),
    HACKER_LAPTOP("hacker_laptop", "Hacker Laptop", HardwareCategory.NETWORK, Material.BOOK, 9500, 60, 850, 18, "Attack hardware for the hidden fake BTC hacking menu"),
    PACKET_SNIFFER("packet_sniffer", "Packet Sniffer", HardwareCategory.NETWORK, Material.SPYGLASS, 16500, 85, 1450, 26, "Raises hack attack power"),
    PASSWORD_CRACKER("password_cracker", "Password Cracker Rig", HardwareCategory.NETWORK, Material.SCULK_SENSOR, 36000, 210, 3400, 75, "Strong fake hacking rig"),
    ZERO_DAY_CHIP("zero_day_chip", "Zero-Day Chip", HardwareCategory.NETWORK, Material.ECHO_SHARD, 115000, 360, 9800, 150, "Insane hack attack module"),
    BLACK_HAT_AI("black_hat_ai", "Black Hat AI Module", HardwareCategory.NETWORK, Material.ECHO_SHARD, 250000, 620, 22000, 260, "Endgame hacking power"),
    VPN_ROUTER("vpn_router", "VPN Router", HardwareCategory.NETWORK, Material.REPEATER, 5500, 35, 700, 6, "Security item that hides fake BTC behind routing"),
    FIREWALL_NODE("firewall_node", "Firewall Node", HardwareCategory.NETWORK, Material.TARGET, 14500, 70, 1600, 10, "Security item that raises anti-hack defense"),
    ENCRYPTED_WALLET_MODULE("encrypted_wallet_module", "Encrypted Wallet Module", HardwareCategory.NETWORK, Material.ENDER_CHEST, 42000, 120, 4300, 20, "Security item that protects fake BTC wallet"),
    QUANTUM_FIREWALL("quantum_firewall", "Quantum Firewall", HardwareCategory.NETWORK, Material.RESPAWN_ANCHOR, 160000, 420, 15000, 90, "Endgame fake BTC defense"),

    CPU_AIR_COOLER("cpu_air_cooler", "CPU Air Cooler", HardwareCategory.COOLING_PART, Material.FEATHER, 150, 8, 0, -20, "Simple cooler"),
    CPU_LIQUID_COOLER("cpu_liquid_cooler", "CPU Liquid Cooler", HardwareCategory.COOLING_PART, Material.POTION, 700, 18, 0, -80, "Liquid cooler"),
    CASE_FAN_120MM("case_fan_120mm", "120mm Case Fan", HardwareCategory.COOLING_PART, Material.STRING, 80, 5, 0, -18, "Case fan"),
    RADIATOR_360MM("radiator_360mm", "360mm Radiator", HardwareCategory.COOLING_PART, Material.IRON_BARS, 1600, 35, 0, -250, "Big radiator"),
    LIQUID_METAL_PASTE("liquid_metal_paste", "Liquid Metal Paste", HardwareCategory.COOLING_PART, Material.GHAST_TEAR, 950, 0, 0, -65, "High-end thermal compound"),
    THERMAL_PASTE("thermal_paste", "Thermal Paste", HardwareCategory.COOLING_PART, Material.SLIME_BALL, 70, 0, 0, -10, "Tiny cooling boost"),

    BITCOIN_USB("bitcoin_usb", "Bitcoin USB", HardwareCategory.TOOL, Material.LIGHTNING_ROD, 250000, 0, 0, 0, "Expensive cold-storage USB for your private fake BTC code"),
    DIAGNOSTIC_SCANNER("diagnostic_scanner", "Diagnostic Scanner", HardwareCategory.TOOL, Material.CLOCK, 1800, 0, 0, 0, "Shows machine status and helps players find problems"),
    MACHINE_TRACKER("machine_tracker", "Machine Tracker", HardwareCategory.TOOL, Material.COMPASS, 1500, 0, 0, 0, "Track machine areas and ownership"),
    REMOTE_MONITOR_TABLET("remote_monitor_tablet", "Remote Monitor Tablet", HardwareCategory.TOOL, Material.FILLED_MAP, 6500, 0, 0, 0, "Portable monitor for rich operators"),
    POWER_USAGE_SCANNER("power_usage_scanner", "Power Usage Scanner", HardwareCategory.TOOL, Material.REDSTONE_TORCH, 1200, 0, 0, 0, "Scans what is eating watts"),
    HEAT_SCANNER("heat_scanner", "Heat Scanner", HardwareCategory.TOOL, Material.BLAZE_POWDER, 1200, 0, 0, 0, "Scans heat problems"),
    PROFIT_SCANNER("profit_scanner", "Profit Scanner", HardwareCategory.TOOL, Material.LIME_DYE, 2500, 0, 0, 0, "Scans money/BTC production"),

    HARDWARE_SECURITY_KEY("hardware_security_key", "Hardware Security Key", HardwareCategory.NETWORK, Material.TRIPWIRE_HOOK, 24000, 18, 2600, 2, "Strong fake BTC account security key"),
    COLD_STORAGE_DRIVE("cold_storage_drive", "Cold Storage Drive", HardwareCategory.STORAGE, Material.PRISMARINE_SHARD, 52000, 12, 4400, 1, "Stores wallet data offline and adds major defense"),
    HONEYPOT_SERVER("honeypot_server", "Honeypot Server", HardwareCategory.NETWORK, Material.HONEY_BLOCK, 78000, 160, 7200, 45, "Traps weak hackers and raises wallet defense"),
    ZERO_TRUST_CORE("zero_trust_core", "Zero Trust Core", HardwareCategory.NETWORK, Material.ECHO_SHARD, 210000, 260, 18000, 65, "Extreme security device"),
    MULTISIG_WALLET_MODULE("multisig_wallet_module", "Multisig Wallet Module", HardwareCategory.STORAGE, Material.ECHO_SHARD, 125000, 85, 11000, 20, "Requires multiple fake signatures to steal BTC"),

    OVERCLOCK_MODULE("overclock_module", "Overclock Module", HardwareCategory.MODULE, Material.BLAZE_ROD, 18000, 180, 2400, 220, "More mining speed, much more power and heat"),
    EXTREME_OVERCLOCK_MODULE("extreme_overclock_module", "Extreme Overclock Module", HardwareCategory.MODULE, Material.BLAZE_POWDER, 65000, 460, 8500, 620, "Crazy speed with dangerous heat"),
    THERMAL_OPTIMIZER("thermal_optimizer", "Thermal Optimizer", HardwareCategory.MODULE, Material.PRISMARINE_CRYSTALS, 24000, 40, 900, -120, "Helps overclocking stay stable"),
    AUTO_COLLECT_MODULE("auto_collect_module", "Auto Collect Module", HardwareCategory.MODULE, Material.HOPPER, 32000, 45, 250, 12, "Moves generated BTC from a machine into your wallet"),
    AUTO_SELL_MODULE("auto_sell_module", "Auto Sell Module", HardwareCategory.MODULE, Material.LIME_DYE, 65000, 75, 350, 20, "If you are online, auto-sells wallet BTC after auto-collect"),
    QUANTUM_HASH_MODULE("quantum_hash_module", "Quantum Hash Module", HardwareCategory.MODULE, Material.AMETHYST_SHARD, 220000, 900, 26000, 480, "Endgame mining accelerator");

    private final String id;
    private final String displayName;
    private final HardwareCategory category;
    private final Material material;
    private final double cost;
    private final double watts;
    private final double performance;
    private final double heat;
    private final String description;

    HardwareKind(String id, String displayName, HardwareCategory category, Material material, double cost, double watts, double performance, double heat, String description) {
        this.id = id;
        this.displayName = displayName;
        this.category = category;
        this.material = material;
        this.cost = cost;
        this.watts = watts;
        this.performance = performance;
        this.heat = heat;
        this.description = description;
    }

    public String id() { return id; }
    public String displayName() { return displayName; }
    public HardwareCategory category() { return category; }
    public Material material() { return material; }
    public double cost() { return cost; }
    public double watts() { return watts; }
    public double performance() { return performance; }
    public double heat() { return heat; }
    public String description() { return description; }

    public boolean isBitcoinUsb() { return this == BITCOIN_USB; }

    public boolean isScannerTool() {
        return switch (this) {
            case DIAGNOSTIC_SCANNER, MACHINE_TRACKER, REMOTE_MONITOR_TABLET, POWER_USAGE_SCANNER, HEAT_SCANNER, PROFIT_SCANNER -> true;
            default -> false;
        };
    }

    public boolean isModule() { return category == HardwareCategory.MODULE; }

    public boolean isRepairItem() {
        return switch (this) {
            case REPAIR_KIT, REPAIR_BOX, ADVANCED_REPAIR_BOX, EMERGENCY_REPAIR_CRATE -> true;
            default -> false;
        };
    }

    public double repairAmount() {
        return switch (this) {
            case REPAIR_KIT -> 45.0;
            case REPAIR_BOX -> 70.0;
            case ADVANCED_REPAIR_BOX -> 155.0;
            case EMERGENCY_REPAIR_CRATE -> 400.0;
            default -> 0.0;
        };
    }

    public boolean isHackingTool() {
        return switch (this) {
            case HACKER_LAPTOP, PACKET_SNIFFER, PASSWORD_CRACKER, ZERO_DAY_CHIP, BLACK_HAT_AI -> true;
            default -> false;
        };
    }

    public boolean isSecurityTool() {
        return switch (this) {
            case VPN_ROUTER, FIREWALL_NODE, ENCRYPTED_WALLET_MODULE, QUANTUM_FIREWALL, HARDWARE_SECURITY_KEY, COLD_STORAGE_DRIVE, HONEYPOT_SERVER, ZERO_TRUST_CORE, MULTISIG_WALLET_MODULE -> true;
            default -> false;
        };
    }

    public static HardwareKind byId(String id) {
        for (HardwareKind kind : values()) {
            if (kind.id.equalsIgnoreCase(id) || kind.name().equalsIgnoreCase(id)) return kind;
        }
        return null;
    }

    public static List<HardwareKind> byCategory(HardwareCategory category) {
        return Arrays.stream(values()).filter(k -> k.category == category).toList();
    }
}
