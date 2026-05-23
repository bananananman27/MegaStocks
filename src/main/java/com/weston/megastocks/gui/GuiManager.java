package com.weston.megastocks.gui;

import com.weston.megastocks.MegaStocksPlugin;
import com.weston.megastocks.data.PlayerData;
import com.weston.megastocks.item.HardwareCategory;
import com.weston.megastocks.item.HardwareKind;
import com.weston.megastocks.machine.MachineCategory;
import com.weston.megastocks.machine.MachineData;
import com.weston.megastocks.machine.MachineKind;
import com.weston.megastocks.machine.NetworkStats;
import com.weston.megastocks.market.StockData;
import com.weston.megastocks.util.LocationKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class GuiManager {
    private final MegaStocksPlugin plugin;
    public GuiManager(MegaStocksPlugin plugin) { this.plugin = plugin; }

    public void openMain(Player player) {
        Inventory inv = create(MenuType.MAIN, "", 27, "MegaStocks");
        inv.setItem(10, button(Material.YELLOW_CONCRETE, "&6Machine Shop", "&7Buy miners, generators, coolers, stations, racks."));
        inv.setItem(11, button(Material.DIAMOND, "&bHardware Shop", "&7Organized GPUs, CPUs, RAM, PSUs, repair boxes."));
        inv.setItem(12, button(Material.EMERALD, "&aStock Market", "&7Buy/sell fake stocks, view portfolio,", "&7market tools, and the hidden terminal."));
        inv.setItem(13, button(Material.COMPASS, "&eResource Monitor", "&7See what is taking power, heat, BTC, and hardware."));
        inv.setItem(14, button(Material.NETHER_STAR, "&dWallet", "&7Vault balance, fake BTC, and shares."));
        inv.setItem(15, button(Material.REDSTONE, "&cMining Control", "&7Collect BTC and toggle machines."));
        inv.setItem(16, button(Material.ANVIL, "&9Machine List", "&7View machines and install hardware."));
        inv.setItem(22, button(Material.LODESTONE, "&3Areas / Monitors", "&7Make different areas like Mine Room,", "&7Server Room, Bank, or Stock Floor.", "&7New placed machines go into your active area."));
        player.openInventory(inv);
    }

    public void openShop(Player player) {
        Inventory inv = create(MenuType.SHOP_MENU, "", 27, "Machine Shop");
        int slot = 10;
        for (MachineCategory category : MachineCategory.values()) inv.setItem(slot++, machineCategoryIcon(category));
        inv.setItem(26, backButton("&7Back to main menu."));
        player.openInventory(inv);
    }

    public void openMachineCategory(Player player, MachineCategory category) {
        Inventory inv = create(MenuType.SHOP, category.name(), 54, "Machines: " + nice(category.name()));
        int slot = 0;
        for (MachineKind kind : MachineKind.byCategory(category)) {
            if (slot >= 45) break;
            inv.setItem(slot++, shopMachineIcon(kind));
        }
        inv.setItem(49, backButton("&7Back to machine categories."));
        player.openInventory(inv);
    }

    public void openHardwareShop(Player player) {
        Inventory inv = create(MenuType.HARDWARE_MENU, "", 54, "Hardware Shop");
        int slot = 9;
        for (HardwareCategory category : HardwareCategory.values()) {
            if (category == HardwareCategory.CURRENCY) continue;
            inv.setItem(slot++, hardwareCategoryIcon(category));
        }
        inv.setItem(53, backButton("&7Back to main menu."));
        player.openInventory(inv);
    }

    public void openHardwareCategory(Player player, HardwareCategory category) {
        Inventory inv = create(MenuType.HARDWARE_SHOP, category.name(), 54, "Hardware: " + nice(category.name()));
        int slot = 0;
        for (HardwareKind kind : HardwareKind.byCategory(category)) {
            if (kind.category() == HardwareCategory.CURRENCY) continue;
            if (slot >= 45) break;
            inv.setItem(slot++, shopHardwareIcon(kind));
        }
        inv.setItem(49, backButton("&7Back to hardware categories."));
        player.openInventory(inv);
    }

    public void openMarket(Player player) { openMarket(player, 0); }

    public void openMarket(Player player, int page) {
        List<StockData> stocks = new ArrayList<>(plugin.market().stocks());
        stocks.sort(Comparator.comparing(StockData::symbol));
        int pageSize = 45;
        int maxPage = Math.max(0, (stocks.size() - 1) / pageSize);
        int safePage = Math.max(0, Math.min(page, maxPage));
        Inventory inv = create(MenuType.MARKET, String.valueOf(safePage), 54, "MegaStocks Market " + (safePage + 1) + "/" + (maxPage + 1));
        PlayerData data = plugin.data().player(player.getUniqueId(), player.getName());
        int start = safePage * pageSize;
        int slot = 0;
        for (int i = start; i < stocks.size() && slot < pageSize; i++) {
            StockData stock = stocks.get(i);
            inv.setItem(slot++, stockIcon(stock, data.sharesOf(stock.symbol())));
        }
        if (stocks.isEmpty()) inv.setItem(22, button(Material.BARRIER, "&cNo stocks listed", "&7Admins can create stocks with", "&e/mega admin stock create <symbol> <price> <name>"));
        inv.setItem(45, backButton("&7Back to main menu."));
        inv.setItem(46, button(Material.ARROW, "&ePrevious Page", "&7Page: &f" + (safePage + 1) + "&7/&f" + (maxPage + 1), safePage <= 0 ? "&8Already at first page." : "&aClick for page " + safePage));
        inv.setItem(47, button(Material.BOOK, "&fTrading Controls", "&7Left-click: buy 1 share.", "&7Shift-left: buy 10 shares.", "&7Right-click: sell 1 share.", "&7Shift-right: sell 10 shares."));
        inv.setItem(48, button(Material.CHEST, "&ePortfolio", holdingsLore(data)));
        inv.setItem(49, button(Material.LIGHTNING_ROD, "&6Bitcoin USB", data.hasBitcoinCode() ? "&7Your code is set." : "&cSet /code before buying mining gear.", "&7Very expensive cold-storage item.", "&7Buy it from Hardware > Tool."));
        inv.setItem(50, button(Material.LIME_CONCRETE, "&aMarket Sectors", "&7Mining, power, security, AI, banking,", "&7cooling, scanners, and tech stocks.", "&7Admins: /mega admin stock create ..."));
        inv.setItem(51, button(Material.PAPER, "&fPrivate Code", data.hasBitcoinCode() ? "&aCode is set. Use /code to view only yours." : "&cNo code set. Use /code set <code>.", "&7The GUI never prints the full code."));
        inv.setItem(52, button(Material.ARROW, "&eNext Page", "&7Page: &f" + (safePage + 1) + "&7/&f" + (maxPage + 1), safePage >= maxPage ? "&8Already at last page." : "&aClick for page " + (safePage + 2)));
        inv.setItem(53, button(Material.SCULK_SENSOR, "&8???", "&8Hidden terminal detected.", "&7Left/right click targets inside it.", "&cAdmins can keep hacking off by default."));
        player.openInventory(inv);
    }

    public void openHacking(Player player) {
        Inventory inv = create(MenuType.HACKING, "", 54, "Hidden Hacker Terminal");
        PlayerData pd = plugin.data().player(player.getUniqueId(), player.getName());
        double attack = plugin.hacking().attackPower(player.getUniqueId());
        double security = plugin.hacking().securityPower(player.getUniqueId());
        int cooldown = plugin.hacking().cooldownRemainingSeconds(player);
        inv.setItem(0, button(Material.SCULK_SENSOR, "&5Attack Power", "&7Your attack power: &d" + String.format("%,.0f", attack), "&7Install hacker laptops, crackers,", "&7zero-day chips, and black-hat AI."));
        inv.setItem(2, button(Material.ENDER_CHEST, "&bYour Wallet Security", "&7Your defense: &b" + String.format("%,.0f", security), "&7Install VPN routers, firewall nodes,", "&7encrypted wallet modules, and quantum firewalls."));
        inv.setItem(4, button(Material.GOLD_NUGGET, "&6Your Fake BTC", "&7Wallet: &b" + plugin.msg().btc(pd.fakeBtc()), cooldown > 0 ? "&cCooldown: " + cooldown + "s" : "&aReady to attempt."));
        inv.setItem(6, button(Material.REDSTONE_TORCH, "&cHow Hacking Works", "&7This is only simulated gameplay.", "&7Left-click target: wallet hack.", "&7Right-click target: code hack.", "&7Code hacks need INSANE power and heat."));
        inv.setItem(8, button(plugin.hacking().enabled() ? Material.LIME_DYE : Material.RED_DYE, plugin.hacking().enabled() ? "&aHacking Enabled" : "&cHacking Disabled", "&7Admins can toggle this in config", "&7or with /mega hacking toggle."));

        int slot = 9;
        int targets = 0;
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.getUniqueId().equals(player.getUniqueId())) continue;
            if (slot >= 45) break;
            PlayerData targetData = plugin.data().player(target.getUniqueId(), target.getName());
            inv.setItem(slot++, hackingTargetIcon(target, targetData, attack));
            targets++;
        }
        if (targets == 0) inv.setItem(31, button(Material.BARRIER, "&cNo online targets", "&7Targets must be online for the hidden hacking menu."));
        inv.setItem(49, button(Material.BOOK, "&dUpgrade Tips", "&7Attack: Hacker Laptop -> Password Cracker -> Zero-Day Chip.", "&7Defense: VPN Router -> Firewall Node -> Quantum Firewall.", "&7Put these into server racks, bank vaults, or miners."));
        inv.setItem(53, backButton("&7Back to stock market."));
        player.openInventory(inv);
    }

    public void openAreaList(Player player) {
        Inventory inv = create(MenuType.AREA_LIST, "", 54, "MegaStocks Areas");
        String active = plugin.areas().activeArea(player);
        inv.setItem(0, button(Material.LODESTONE, "&3Active Placement Area", "&7Current: &b" + plugin.areas().displayName(active), "&7New machines you place are assigned here.", "&7Use &e/mega area set <name> &7to change it."));
        inv.setItem(4, areaIcon("all", "All Areas", player.getUniqueId(), true));
        inv.setItem(8, button(Material.NAME_TAG, "&eArea Commands", "&7/mega area create <name>", "&7/mega area set <name>", "&7/mega area assign", "&7/mega area station <name>", "&7/mega area monitor <name>"));
        int slot = 9;
        for (String id : plugin.areas().areasFor(player.getUniqueId())) {
            if (slot >= 45) break;
            inv.setItem(slot++, areaIcon(id, plugin.areas().displayName(id), player.getUniqueId(), false));
        }
        inv.setItem(49, backButton("&7Back to main menu."));
        player.openInventory(inv);
    }

    public void openMonitor(Player player) { openMonitor(player, null); }

    public void openMonitor(Player player, String areaId) {
        boolean filtered = areaId != null && !areaId.isBlank() && !"all".equalsIgnoreCase(areaId);
        String normalized = filtered ? plugin.areas().sanitize(areaId) : null;
        NetworkStats stats = plugin.machines().statsFor(player.getUniqueId(), normalized);
        PlayerData pd = plugin.data().player(player.getUniqueId(), player.getName());
        String title = filtered ? "Monitor: " + plugin.areas().displayName(normalized) : "Resource Monitor";
        Inventory inv = create(MenuType.MONITOR, filtered ? normalized : "all", 54, title);
        inv.setItem(0, button(Material.COMPASS, filtered ? "&eArea Summary" : "&eNetwork Summary", "&7Area: &b" + (filtered ? plugin.areas().displayName(normalized) : "All Areas"), "&7Machines: &f" + stats.totalMachines, "&7Miners: &f" + stats.miners, "&7Generators: &f" + stats.generators, "&7Coolers: &f" + stats.coolers, "&7Wires/Pipes: &f" + stats.wires, "&7Server Coolers: &b" + stats.serverCoolingBlocks, "&7Stations: &f" + stats.stations, "&7Hardware Installed: &b" + stats.hardwareInstalled));
        inv.setItem(2, button(Material.REDSTONE, "&cPower", "&7Base Produced: &e" + String.format("%,.0fW", stats.powerProduced), "&7Wire Boosted: &e" + String.format("%,.0fW", stats.effectivePowerProduced()), "&7Used: &e" + String.format("%,.0fW", stats.powerUsed), "&7Free: &e" + String.format("%,.0fW", stats.freePower()), "&7Wire Bonus: &a" + String.format("%.0f%%", (stats.wirePowerMultiplier() - 1.0) * 100.0), stats.powerDeficit() ? "&cPOWER DEFICIT" : "&aPower stable"));
        inv.setItem(4, button(Material.BLUE_ICE, "&bCooling / Heat", "&7Base Cooling: &b" + String.format("%,.0f", stats.coolingProduced), "&7Wire Boosted: &b" + String.format("%,.0f", stats.effectiveCoolingProduced()), "&7Heat: &c" + String.format("%,.0f", stats.heatProduced), "&7Net: &f" + String.format("%,.0f", stats.netCooling()), "&7Pipe Bonus: &a" + String.format("%.0f%%", (stats.wireCoolingMultiplier() - 1.0) * 100.0), stats.coolingDeficit() ? "&cCOOLING DEFICIT" : "&aCooling stable"));
        inv.setItem(6, button(Material.GOLD_NUGGET, "&6Crypto Output", "&7Hashrate: &d" + String.format("%,.0f", stats.totalHashRate), "&7BTC/min: &b" + String.format("%.8f", stats.btcPerMinute), "&7Stored in machines: &b" + plugin.msg().btc(stats.storedBtc), "&7Wallet BTC: &b" + plugin.msg().btc(pd.fakeBtc())));
        inv.setItem(8, button(Material.EMERALD, "&aVault Money", "&7Balance: &a" + plugin.msg().money(plugin.economy().balance(player)), "&7Provider: &f" + plugin.economy().providerName()));
        int slot = 18;
        inv.setItem(9, button(Material.YELLOW_WOOL, "&eTop Power Users"));
        for (MachineData d : stats.topPowerUsers) { if (slot > 26) break; inv.setItem(slot++, smallMachineIcon(d, "&ePower: " + String.format("%,.0fW", d.effectivePowerUse()))); }
        slot = 36;
        inv.setItem(27, button(Material.RED_WOOL, "&cTop Heat Makers"));
        for (MachineData d : stats.topHeatMakers) { if (slot > 44) break; inv.setItem(slot++, smallMachineIcon(d, "&cHeat: " + String.format("%,.0f", d.effectiveHeat()))); }
        inv.setItem(45, button(Material.LODESTONE, "&3Areas", "&7Open area list and area monitors."));
        inv.setItem(48, button(Material.LIGHTNING_ROD, "&ePower Management", "&7Click to open power controls", filtered ? "&7for this area." : "&7for all areas."));
        inv.setItem(49, button(Material.HOPPER, "&aCollect Area BTC", filtered ? "&7Collect stored BTC from this area only." : "&7Collect stored BTC from all machines."));
        inv.setItem(50, button(Material.ANVIL, "&9Machine List", "&7Install GPUs, CPUs, RAM, and more."));
        inv.setItem(53, backButton("&7Back to main menu."));
        player.openInventory(inv);
    }

    public void openWallet(Player player) {
        Inventory inv = create(MenuType.WALLET, "", 27, "MegaStocks Wallet");
        PlayerData pd = plugin.data().player(player.getUniqueId(), player.getName());
        inv.setItem(10, button(Material.EMERALD, "&aVault Balance", "&7" + plugin.msg().money(plugin.economy().balance(player)), "&7Provider: &f" + plugin.economy().providerName()));
        inv.setItem(12, button(Material.GOLD_NUGGET, "&6Fake BTC Wallet", "&7" + plugin.msg().btc(pd.fakeBtc()), "&7Sell price: &a" + plugin.msg().money(plugin.machines().currentBtcSellPrice()) + " / BTC", pd.hasBitcoinCode() ? "&aPrivate code set. Use /code." : "&cNo private code. Use /code set <code>."));
        inv.setItem(14, button(Material.GOLD_INGOT, "&eSell All Fake BTC", "&7Click to sell wallet BTC using Vault."));
        inv.setItem(16, button(Material.PAPER, "&fStock Holdings", holdingsLore(pd)));
        inv.setItem(26, backButton("&7Back to main menu."));
        player.openInventory(inv);
    }

    public void openPower(Player player) { openPower(player, null); }

    public void openPower(Player player, String areaId) {
        boolean filtered = areaId != null && !areaId.isBlank() && !"all".equalsIgnoreCase(areaId);
        String normalized = filtered ? plugin.areas().sanitize(areaId) : null;
        Inventory inv = create(MenuType.POWER, filtered ? normalized : "all", 27, filtered ? "Power: " + plugin.areas().displayName(normalized) : "Power Management");
        NetworkStats stats = plugin.machines().statsFor(player.getUniqueId(), normalized);
        inv.setItem(10, button(Material.RED_CONCRETE, "&cPower Usage", "&7Area: &b" + (filtered ? plugin.areas().displayName(normalized) : "All Areas"), "&7Produced: &e" + String.format("%,.0fW", stats.effectivePowerProduced()), "&7Base: &7" + String.format("%,.0fW", stats.powerProduced), "&7Used: &e" + String.format("%,.0fW", stats.powerUsed), "&7Free: &e" + String.format("%,.0fW", stats.freePower()), "&7Wires: &f" + stats.wires));
        inv.setItem(12, button(Material.LEVER, "&eToggle Area Miners", filtered ? "&7Click to turn miners in this area on/off." : "&7Click to turn all miners on/off."));
        inv.setItem(14, button(Material.BLUE_ICE, "&bCooling", "&7Cooling: &b" + String.format("%,.0f", stats.effectiveCoolingProduced()), "&7Base: &7" + String.format("%,.0f", stats.coolingProduced), "&7Heat: &c" + String.format("%,.0f", stats.heatProduced), "&7Wires/Pipes: &f" + stats.wires));
        inv.setItem(16, button(Material.COMPASS, "&eResource Monitor", "&7See top power users and heat makers."));
        inv.setItem(26, backButton("&7Back to resource monitor."));
        player.openInventory(inv);
    }

    public void openMiningControl(Player player) { openMiningControl(player, null); }

    public void openMiningControl(Player player, String areaId) {
        boolean filtered = areaId != null && !areaId.isBlank() && !"all".equalsIgnoreCase(areaId);
        String normalized = filtered ? plugin.areas().sanitize(areaId) : null;
        Inventory inv = create(MenuType.MINING_CONTROL, filtered ? normalized : "all", 27, filtered ? "Mining: " + plugin.areas().displayName(normalized) : "Mining Control");
        NetworkStats stats = plugin.machines().statsFor(player.getUniqueId(), normalized);
        inv.setItem(10, button(Material.GOLD_NUGGET, "&6Collect Area BTC", "&7Stored: &b" + plugin.msg().btc(stats.storedBtc), filtered ? "&7Collect only this area." : "&7Collect all areas."));
        inv.setItem(12, button(Material.EMERALD, "&aCollect + Sell BTC", "&7Collects this scope and sells wallet BTC."));
        inv.setItem(14, button(Material.LEVER, "&eToggle Area Miners", "&7Click to switch miner enabled status."));
        inv.setItem(16, button(Material.COMPASS, "&dView Monitor", "&7Open resource monitor."));
        inv.setItem(26, backButton("&7Back to main menu."));
        player.openInventory(inv);
    }

    public void openMachine(Player player, MachineData data) {
        Inventory inv = create(MenuType.MACHINE, data.locationKey(), 36, data.kind().displayName());
        inv.setItem(4, smallMachineIcon(data, "&7Location: &f" + LocationKey.pretty(data.locationKey())));
        inv.setItem(10, button(data.enabled() ? Material.LIME_DYE : Material.GRAY_DYE, data.enabled() ? "&aEnabled" : "&7Disabled", "&7Click to toggle this machine."));
        inv.setItem(11, button(Material.GOLD_NUGGET, "&6Collect BTC", "&7Stored: &b" + plugin.msg().btc(data.storedBtc()), "&7Click to move BTC into your wallet."));
        inv.setItem(12, button(Material.ANVIL, "&9Upgrade", "&7Level: &f" + data.level(), "&7Cost: &a" + plugin.msg().money(upgradeCost(data)), "&7+18% production, +10% power."));
        inv.setItem(13, button(Material.LODESTONE, "&3Area", "&7Area: &b" + plugin.areas().displayName(data.areaId()), "&7Use &e/mega area assign &7while looking", "&7at this machine to move it."));
        inv.setItem(14, button(Material.CHEST, "&eRepair Uses Boxes", "&7No money repair here.", "&7Hold a repair item and right-click the placed machine."));
        inv.setItem(15, button(Material.BOOK, "&bHardware Help", "&aLeft-click a slot: install a matching part.", "&eRight-click a slot: remove the last part.", "&7You can also left-click hardware in your inventory.", "&bBedrock/Geyser: look at this block and use", "&e/mega install gpu &7or &e/mega remove gpu&7."));
        setHardwareSlot(inv, data, 19, HardwareCategory.GPU);
        setHardwareSlot(inv, data, 20, HardwareCategory.CPU);
        setHardwareSlot(inv, data, 21, HardwareCategory.RAM);
        setHardwareSlot(inv, data, 22, HardwareCategory.MOTHERBOARD);
        setHardwareSlot(inv, data, 23, HardwareCategory.PSU);
        setHardwareSlot(inv, data, 24, HardwareCategory.STORAGE);
        setHardwareSlot(inv, data, 25, HardwareCategory.NETWORK);
        setHardwareSlot(inv, data, 26, HardwareCategory.COOLING_PART);
        setHardwareSlot(inv, data, 34, HardwareCategory.MODULE);
        inv.setItem(35, backButton("&7Back to machine list."));
        player.openInventory(inv);
    }

    public void openMachineList(Player player) {
        Inventory inv = create(MenuType.MACHINE_LIST, "", 54, "Your Machines");
        int limit = plugin.getConfig().getInt("performance.max-machine-gui-list", 45);
        List<MachineData> owned = plugin.data().allMachines().stream()
                .filter(m -> m.owner().equals(player.getUniqueId()))
                .sorted((a, b) -> {
                    int areaCompare = a.areaId().compareToIgnoreCase(b.areaId());
                    if (areaCompare != 0) return areaCompare;
                    int categoryCompare = a.kind().category().name().compareToIgnoreCase(b.kind().category().name());
                    if (categoryCompare != 0) return categoryCompare;
                    return a.kind().displayName().compareToIgnoreCase(b.kind().displayName());
                })
                .limit(Math.min(limit, 45))
                .toList();
        int slot = 0;
        for (MachineData data : owned) inv.setItem(slot++, smallMachineIcon(data, "&7Click to manage and install hardware."));
        if (owned.isEmpty()) inv.setItem(22, button(Material.BARRIER, "&cNo machines", "&7Buy a machine from /megastocks."));
        inv.setItem(53, backButton("&7Back to main menu."));
        player.openInventory(inv);
    }

    public double upgradeCost(MachineData data) { return Math.round(plugin.machinePrice(data.kind()) * 0.35 * data.level() * data.level()); }
    public double repairCost(MachineData data) { return Math.max(25.0, Math.round(plugin.machinePrice(data.kind()) * 0.08)); }
    private Inventory create(MenuType type, String data, int size, String title) { MenuHolder holder = new MenuHolder(type, data == null ? "" : data); Inventory inv = Bukkit.createInventory(holder, size, ChatColor.DARK_GRAY + title); holder.inventory(inv); return inv; }
    private void setHardwareSlot(Inventory inv, MachineData data, int slot, HardwareCategory category) { inv.setItem(slot, hardwareSlotIcon(data, category)); }

    private ItemStack hardwareSlotIcon(MachineData data, HardwareCategory category) {
        List<String> lore = new ArrayList<>();
        lore.add("&7Slot: &f" + nice(category.name()));
        lore.add("&7Installed: &e" + data.installedCount(category) + "&7/&e" + data.maxSlots(category));
        if (!data.supportsHardware()) lore.add("&cThis machine cannot accept hardware.");
        for (HardwareKind h : data.installed(category)) lore.add("&8- &b" + h.displayName());
        lore.add("");
        lore.add("&aLeft-click: install matching part.");
        lore.add("&eRight-click: remove last installed part.");
        lore.add("&7Works from cursor, main hand, or inventory.");
        lore.add("&bBedrock backup: /mega install " + categoryCommand(category));
        lore.add("&bRemove backup: /mega remove " + categoryCommand(category));
        return button(categoryMaterial(category), "&b" + nice(category.name()) + " Slot", lore.toArray(new String[0]));
    }

    private ItemStack areaIcon(String areaId, String displayName, java.util.UUID owner, boolean all) {
        String id = all ? "all" : plugin.areas().sanitize(areaId);
        String name = all ? "&eAll Areas Monitor" : "&3" + displayName;
        String count = all ? String.valueOf(plugin.machines().statsFor(owner).totalMachines) : String.valueOf(plugin.areas().machineCount(owner, id));
        return button(all ? Material.COMPASS : Material.LODESTONE, name, "&7Machines: &f" + count, "&aLeft-click: open monitor.", "&eRight-click: set active placement area.", all ? "&7All areas cannot be used for placement." : "&7New machines can be placed into this area.", "&8AREA=" + id);
    }

    private ItemStack machineCategoryIcon(MachineCategory category) {
        Material material = switch (category) { case MINER -> Material.DIAMOND_PICKAXE; case GENERATOR -> Material.RED_CONCRETE; case COOLING -> Material.BLUE_ICE; case WIRE -> Material.LIGHTNING_ROD; case STATION -> Material.LECTERN; case STORAGE -> Material.CHEST; case DECORATION -> Material.SEA_LANTERN; };
        return button(material, "&6" + nice(category.name()), "&7Items in category: &e" + MachineKind.byCategory(category).size(), category == MachineCategory.WIRE ? "&7Easy: place wires/pipes in the same area." : "&7Click to open.", category == MachineCategory.WIRE ? "&7They boost area power/cooling automatically." : "");
    }
    private ItemStack hardwareCategoryIcon(HardwareCategory category) { return button(categoryMaterial(category), "&b" + nice(category.name()), "&7Items in category: &e" + HardwareKind.byCategory(category).size(), "&7Click to open."); }
    private String categoryCommand(HardwareCategory category) { return category == HardwareCategory.COOLING_PART ? "cooling" : category.name().toLowerCase(); }
    private Material categoryMaterial(HardwareCategory category) {
        return switch (category) { case GPU -> Material.DIAMOND; case CPU -> Material.COMPARATOR; case RAM -> Material.LIME_DYE; case MOTHERBOARD -> Material.GREEN_CARPET; case PSU -> Material.RED_CONCRETE; case STORAGE -> Material.CHEST; case NETWORK -> Material.COMPASS; case MODULE -> Material.BLAZE_ROD; case COOLING_PART -> Material.BLUE_ICE; case MATERIAL -> Material.ECHO_SHARD; case TOOL -> Material.TRIPWIRE_HOOK; case CURRENCY -> Material.GOLD_NUGGET; };
    }
    private ItemStack shopMachineIcon(MachineKind kind) { ItemStack item = plugin.items().machineItem(kind, 1); ItemMeta meta = item.getItemMeta(); List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>(); lore.add(""); lore.add(ChatColor.GREEN + "Click to buy with Vault money."); meta.setLore(lore); item.setItemMeta(meta); return item; }
    private ItemStack shopHardwareIcon(HardwareKind kind) { ItemStack item = plugin.items().hardwareItem(kind, 1); ItemMeta meta = item.getItemMeta(); List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>(); lore.add(""); lore.add(ChatColor.GREEN + "Click to buy with Vault money."); if (kind.isRepairItem()) lore.add(ChatColor.YELLOW + "Use: right-click your placed machine."); if (kind.isHackingTool()) lore.add(ChatColor.DARK_PURPLE + "Hidden use: raises fake hacking attack power."); if (kind.isSecurityTool()) lore.add(ChatColor.AQUA + "Security use: protects fake BTC from hacks."); if (kind.isModule()) lore.add(ChatColor.LIGHT_PURPLE + "Module: install into machine module slot."); if (kind.isBitcoinUsb()) lore.add(ChatColor.GOLD + "Requires your /code first; stores backup item data."); meta.setLore(lore); item.setItemMeta(meta); return item; }
    private ItemStack hackingTargetIcon(Player target, PlayerData targetData, double attack) { double security = plugin.hacking().securityPower(target.getUniqueId()); double ratio = security <= 0 ? 1.0 : attack / security; Material material = ratio >= 1.0 ? Material.LIME_CONCRETE : ratio >= 0.55 ? Material.YELLOW_CONCRETE : Material.RED_CONCRETE; return button(material, "&cHack &f" + target.getName(), "&7Target fake BTC: &b" + plugin.msg().btc(targetData.fakeBtc()), "&7Target security: &b" + String.format("%,.0f", security), "&7Your attack: &d" + String.format("%,.0f", attack), ratio >= 0.55 ? "&eLeft-click: wallet hack." : "&cToo secure for wallet hack.", "&5Right-click: code hack (MUCH harder).", "&8TARGET=" + target.getUniqueId()); }
    private ItemStack stockIcon(StockData stock, int owned) { Material mat = stock.changePercent() >= 0 ? Material.LIME_CONCRETE : Material.RED_CONCRETE; return button(mat, (stock.changePercent() >= 0 ? "&a" : "&c") + stock.symbol() + " &7- &f" + stock.name(), "&7Price: &a" + plugin.msg().money(stock.price()), "&7Change: " + (stock.changePercent() >= 0 ? "&a" : "&c") + plugin.msg().pct(stock.changePercent()), "&7You own: &e" + owned, "&7News: &f" + stock.lastNews()); }
    private ItemStack smallMachineIcon(MachineData data, String extra) { return button(data.kind().blockMaterial(), "&6" + data.kind().displayName(), "&7ID: &f" + data.kind().id(), "&7Area: &b" + plugin.areas().displayName(data.areaId()), "&7Level: &f" + data.level(), "&7Status: " + (data.enabled() ? "&aEnabled" : "&7Disabled"), "&7Heat: &c" + String.format("%.1f", data.heat()), "&7Power: &e" + String.format("%,.0fW", data.effectivePowerUse()), "&7Hardware: &b" + data.installedCount(), "&7Stored BTC: &b" + plugin.msg().btc(data.storedBtc()), extra, "&8LOC=" + data.locationKey()); }
    private ItemStack backButton(String lore) { return button(Material.ARROW, "&eBack", lore); }
    private ItemStack button(Material material, String name, String... lore) { ItemStack item = new ItemStack(material == null ? Material.STONE : material); ItemMeta meta = item.getItemMeta(); meta.setDisplayName(plugin.msg().color(name)); List<String> lines = new ArrayList<>(); for (String line : lore) lines.add(plugin.msg().color(line)); meta.setLore(lines); meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES); item.setItemMeta(meta); return item; }
    private String[] holdingsLore(PlayerData data) { if (data.shares().isEmpty()) return new String[]{"&7No stock holdings yet."}; List<String> lore = new ArrayList<>(); for (var e : data.shares().entrySet()) lore.add("&7" + e.getKey() + ": &e" + e.getValue() + " shares"); return lore.toArray(new String[0]); }
    private String nice(String raw) { String lower = raw.toLowerCase().replace('_', ' '); StringBuilder out = new StringBuilder(); for (String part : lower.split(" ")) { if (part.isEmpty()) continue; if (out.length() > 0) out.append(' '); out.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)); } return out.toString(); }
}
