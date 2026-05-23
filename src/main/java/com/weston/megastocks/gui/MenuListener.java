package com.weston.megastocks.gui;

import com.weston.megastocks.MegaStocksPlugin;
import com.weston.megastocks.data.PlayerData;
import com.weston.megastocks.item.HardwareCategory;
import com.weston.megastocks.item.HardwareKind;
import com.weston.megastocks.machine.MachineCategory;
import com.weston.megastocks.machine.MachineData;
import com.weston.megastocks.machine.MachineKind;
import com.weston.megastocks.market.StockData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class MenuListener implements Listener {
    private final MegaStocksPlugin plugin;

    public MenuListener(MegaStocksPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof MenuHolder holder)) return;

        event.setCancelled(true);
        if (event.getClickedInventory() == null) return;

        if (event.getClickedInventory() != event.getInventory()) {
            if (holder.type() == MenuType.MACHINE) handleMachineInventoryClick(player, holder.data(), event);
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return;
        if (isBack(clicked)) {
            handleBack(player, holder);
            return;
        }

        switch (holder.type()) {
            case MAIN -> handleMain(player, event.getSlot());
            case SHOP_MENU -> handleMachineCategoryMenu(player, event.getSlot());
            case SHOP -> handleShop(player, clicked);
            case HARDWARE_MENU -> handleHardwareCategoryMenu(player, event.getSlot());
            case HARDWARE_SHOP -> handleHardwareShop(player, clicked);
            case MARKET -> handleMarket(player, clicked, event.getSlot(), event.isLeftClick(), event.isRightClick(), event.isShiftClick());
            case HACKING -> handleHacking(player, clicked, event.isRightClick());
            case AREA_LIST -> handleAreaList(player, clicked, event.isLeftClick(), event.isRightClick());
            case MONITOR -> handleMonitor(player, holder.data(), event.getSlot());
            case WALLET -> handleWallet(player, event.getSlot());
            case POWER -> handlePower(player, holder.data(), event.getSlot());
            case MINING_CONTROL -> handleMiningControl(player, holder.data(), event.getSlot());
            case MACHINE -> handleMachine(player, holder.data(), event);
            case MACHINE_LIST -> handleMachineListClick(player, clicked);
        }
    }

    private void handleBack(Player player, MenuHolder holder) {
        switch (holder.type()) {
            case SHOP -> plugin.gui().openShop(player);
            case HARDWARE_SHOP -> plugin.gui().openHardwareShop(player);
            case MACHINE -> plugin.gui().openMachineList(player);
            case HACKING -> plugin.gui().openMarket(player);
            case POWER -> plugin.gui().openMonitor(player, areaOrNull(holder.data()));
            default -> plugin.gui().openMain(player);
        }
    }

    private void handleMain(Player player, int slot) {
        switch (slot) {
            case 10 -> plugin.gui().openShop(player);
            case 11 -> plugin.gui().openHardwareShop(player);
            case 12 -> plugin.gui().openMarket(player);
            case 13 -> plugin.gui().openMonitor(player);
            case 14 -> plugin.gui().openWallet(player);
            case 15 -> plugin.gui().openMiningControl(player);
            case 16 -> plugin.gui().openMachineList(player);
            case 22 -> plugin.gui().openAreaList(player);
            default -> { }
        }
    }

    private void handleMachineCategoryMenu(Player player, int slot) {
        int index = slot - 10;
        MachineCategory[] values = MachineCategory.values();
        if (index >= 0 && index < values.length) plugin.gui().openMachineCategory(player, values[index]);
    }

    private void handleHardwareCategoryMenu(Player player, int slot) {
        int s = 9;
        for (HardwareCategory category : HardwareCategory.values()) {
            if (category == HardwareCategory.CURRENCY) continue;
            if (s == slot) {
                plugin.gui().openHardwareCategory(player, category);
                return;
            }
            s++;
        }
    }

    private void handleMachineListClick(Player player, ItemStack clicked) {
        String loc = hiddenLoc(clicked);
        if (loc == null) return;
        MachineData data = plugin.data().machine(loc);
        if (data != null && data.owner().equals(player.getUniqueId())) plugin.gui().openMachine(player, data);
    }

    private void handleShop(Player player, ItemStack clicked) {
        String id = plugin.items().readMegaId(clicked);
        MachineKind kind = MachineKind.byId(id == null ? "" : id);
        if (kind == null) return;
        if (kind.isMiner() && !ensureCode(player)) return;
        double price = plugin.machinePrice(kind);
        if (!withdrawCost(player, price)) return;
        player.getInventory().addItem(plugin.items().machineItem(kind, 1));
        plugin.msg().send(player, "&aBought &e" + kind.displayName() + " &afor &e" + plugin.msg().money(price) + "&a.");
    }

    private void handleHardwareShop(Player player, ItemStack clicked) {
        String id = plugin.items().readMegaId(clicked);
        HardwareKind kind = HardwareKind.byId(id == null ? "" : id);
        if (kind == null) return;
        if (requiresCodeForPurchase(kind) && !ensureCode(player)) return;
        double price = plugin.hardwarePrice(kind);
        if (!withdrawCost(player, price)) return;
        if (kind.isBitcoinUsb()) player.getInventory().addItem(plugin.items().bitcoinUsbItem(plugin.data().player(player.getUniqueId(), player.getName()), 1));
        else player.getInventory().addItem(plugin.items().hardwareItem(kind, 1));
        plugin.msg().send(player, "&aBought &b" + kind.displayName() + " &afor &e" + plugin.msg().money(price) + "&a.");
    }

    private void handleMarket(Player player, ItemStack clicked, int slot, boolean left, boolean right, boolean shift) {
        int page = currentMarketPage(player);
        if (slot == 46) { plugin.gui().openMarket(player, Math.max(0, page - 1)); return; }
        if (slot == 52) { plugin.gui().openMarket(player, page + 1); return; }
        if (slot == 53) {
            plugin.gui().openHacking(player);
            return;
        }
        if (slot >= 45) return;
        String symbol = symbolFromName(clicked);
        if (symbol == null) return;
        StockData stock = plugin.market().stock(symbol);
        if (stock == null) return;

        int amount = shift ? plugin.getConfig().getInt("market.shift-buy-amount", 10) : plugin.getConfig().getInt("market.default-buy-amount", 1);
        amount = Math.max(1, amount);
        PlayerData pd = plugin.data().player(player.getUniqueId(), player.getName());

        if (left) {
            double cost = stock.price() * amount;
            if (!withdrawCost(player, cost)) return;
            pd.addShares(symbol, amount);
            plugin.data().markDirty();
            plugin.msg().send(player, "&aBought &e" + amount + " &ashares of &f" + symbol + " &afor &e" + plugin.msg().money(cost) + "&a.");
            plugin.gui().openMarket(player, page);
        } else if (right) {
            int owned = pd.sharesOf(symbol);
            if (owned <= 0) {
                plugin.msg().send(player, "&cYou do not own any &f" + symbol + "&c.");
                return;
            }
            int sell = Math.min(amount, owned);
            double payout = stock.price() * sell;
            if (!plugin.economy().deposit(player, payout)) {
                plugin.msg().send(player, "&cVault economy is not ready or the deposit failed.");
                return;
            }
            pd.addShares(symbol, -sell);
            plugin.data().markDirty();
            plugin.msg().send(player, "&aSold &e" + sell + " &ashares of &f" + symbol + " &afor &e" + plugin.msg().money(payout) + "&a.");
            plugin.gui().openMarket(player, page);
        }
    }

    private int currentMarketPage(Player player) {
        if (player.getOpenInventory().getTopInventory().getHolder() instanceof MenuHolder holder) {
            try { return Math.max(0, Integer.parseInt(holder.data())); }
            catch (NumberFormatException ignored) { return 0; }
        }
        return 0;
    }

    private void handleHacking(Player player, ItemStack clicked, boolean rightClick) {
        String targetRaw = hiddenTarget(clicked);
        if (targetRaw == null) return;
        try {
            java.util.UUID targetUuid = java.util.UUID.fromString(targetRaw);
            var result = rightClick ? plugin.hacking().attemptCodeHack(player, targetUuid) : plugin.hacking().attemptHack(player, targetUuid);
            plugin.msg().send(player, result.message());
            plugin.gui().openHacking(player);
        } catch (IllegalArgumentException ex) {
            plugin.msg().send(player, "&cThat hacking target is invalid.");
        }
    }

    private void handleAreaList(Player player, ItemStack clicked, boolean left, boolean right) {
        String area = hiddenArea(clicked);
        if (area == null) return;
        if (right) {
            if ("all".equalsIgnoreCase(area)) {
                plugin.msg().send(player, "&cAll Areas is a monitor view, not a placement area.");
                return;
            }
            plugin.areas().activeArea(player, area);
            plugin.msg().send(player, "&aActive placement area set to &b" + plugin.areas().displayName(area) + "&a.");
            plugin.gui().openAreaList(player);
            return;
        }
        if (left) plugin.gui().openMonitor(player, areaOrNull(area));
    }

    private void handleMonitor(Player player, String areaId, int slot) {
        String area = areaOrNull(areaId);
        if (slot == 45) plugin.gui().openAreaList(player);
        else if (slot == 48) plugin.gui().openPower(player, area);
        else if (slot == 49) {
            double btc = plugin.machines().collectAllBtc(player, area);
            plugin.msg().send(player, "&aCollected &b" + plugin.msg().btc(btc) + (area == null ? "&a from all machines." : "&a from &b" + plugin.areas().displayName(area) + "&a."));
            plugin.gui().openMonitor(player, area);
        } else if (slot == 50) plugin.gui().openMachineList(player);
    }

    private void handleWallet(Player player, int slot) {
        if (slot != 14) return;
        double sold = plugin.machines().sellAllBtc(player);
        if (sold < 0) plugin.msg().send(player, "&cVault economy is not ready.");
        else plugin.msg().send(player, "&aSold all fake BTC for &e" + plugin.msg().money(sold) + "&a.");
        plugin.gui().openWallet(player);
    }

    private void handlePower(Player player, String areaId, int slot) {
        String area = areaOrNull(areaId);
        if (slot == 12) {
            plugin.machines().toggleMiners(player, area);
            plugin.msg().send(player, area == null ? "&eToggled all miners." : "&eToggled miners in &b" + plugin.areas().displayName(area) + "&e.");
            plugin.gui().openPower(player, area);
        } else if (slot == 16) plugin.gui().openMonitor(player, area);
    }

    private void handleMiningControl(Player player, String areaId, int slot) {
        String area = areaOrNull(areaId);
        if (slot == 10) {
            double btc = plugin.machines().collectAllBtc(player, area);
            plugin.msg().send(player, "&aCollected &b" + plugin.msg().btc(btc) + "&a.");
            plugin.gui().openMiningControl(player, area);
        } else if (slot == 12) {
            double collected = plugin.machines().collectAllBtc(player, area);
            double sold = plugin.machines().sellAllBtc(player);
            if (sold < 0) plugin.msg().send(player, "&cVault economy is not ready.");
            else plugin.msg().send(player, "&aCollected &b" + plugin.msg().btc(collected) + " &aand sold wallet BTC for &e" + plugin.msg().money(sold) + "&a.");
            plugin.gui().openMiningControl(player, area);
        } else if (slot == 14) {
            plugin.machines().toggleMiners(player, area);
            plugin.msg().send(player, area == null ? "&eToggled all miners." : "&eToggled miners in &b" + plugin.areas().displayName(area) + "&e.");
            plugin.gui().openMiningControl(player, area);
        } else if (slot == 16) plugin.gui().openMonitor(player, area);
    }

    private void handleMachine(Player player, String loc, InventoryClickEvent event) {
        MachineData data = plugin.data().machine(loc);
        if (data == null) {
            player.closeInventory();
            plugin.msg().send(player, "&cThat machine no longer exists.");
            return;
        }
        if (!data.owner().equals(player.getUniqueId()) && !player.hasPermission("megastocks.admin")) return;

        int slot = event.getSlot();
        HardwareCategory hardwareSlot = hardwareCategoryForSlot(slot);
        if (hardwareSlot != null) {
            handleHardwareSlot(player, data, hardwareSlot, event);
            return;
        }

        if (slot == 10) {
            data.enabled(!data.enabled());
            plugin.data().markDirty();
            plugin.msg().send(player, "&e" + data.kind().displayName() + " is now " + (data.enabled() ? "&aenabled" : "&7disabled") + "&e.");
            plugin.gui().openMachine(player, data);
        } else if (slot == 11) {
            PlayerData pd = plugin.data().player(player.getUniqueId(), player.getName());
            double btc = data.storedBtc();
            pd.fakeBtc(pd.fakeBtc() + btc);
            data.storedBtc(0.0);
            plugin.data().markDirty();
            plugin.msg().send(player, "&aCollected &b" + plugin.msg().btc(btc) + "&a.");
            plugin.gui().openMachine(player, data);
        } else if (slot == 12) {
            double cost = plugin.gui().upgradeCost(data);
            if (!withdrawCost(player, cost)) return;
            data.level(data.level() + 1);
            plugin.data().markDirty();
            plugin.msg().send(player, "&aUpgraded &e" + data.kind().displayName() + " &ato level &f" + data.level() + "&a.");
            plugin.gui().openMachine(player, data);
        } else if (slot == 14) {
            plugin.msg().send(player, "&eRepairing does not use money. Buy a Repair Box, hold it, then right-click the placed machine.");
        }
    }

    private void handleMachineInventoryClick(Player player, String loc, InventoryClickEvent event) {
        MachineData data = plugin.data().machine(loc);
        if (data == null) return;
        if (!data.owner().equals(player.getUniqueId()) && !player.hasPermission("megastocks.admin")) return;
        if (!event.isLeftClick()) return;

        ItemStack clicked = event.getCurrentItem();
        HardwareKind hardware = hardwareFrom(clicked);
        if (hardware == null || hardware.isRepairItem()) return;
        if (!data.canInstall(hardware)) {
            plugin.msg().send(player, "&cThat part cannot be installed here, or the &e" + nice(hardware.category().name()) + " &cslot is full.");
            return;
        }

        data.install(hardware);
        takeOne(clicked);
        event.setCurrentItem(clicked.getAmount() <= 0 ? new ItemStack(Material.AIR) : clicked);
        plugin.data().markDirty();
        plugin.msg().send(player, "&aInstalled &b" + hardware.displayName() + " &ainto &e" + data.kind().displayName() + "&a.");
        plugin.gui().openMachine(player, data);
    }

    private void handleHardwareSlot(Player player, MachineData data, HardwareCategory slotCategory, InventoryClickEvent event) {
        if (event.isRightClick()) {
            HardwareKind removed = data.removeLast(slotCategory);
            if (removed == null) {
                plugin.msg().send(player, "&cNo installed part in that slot.");
                return;
            }
            player.getInventory().addItem(plugin.items().hardwareItem(removed, 1)).values().forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
            plugin.data().markDirty();
            plugin.msg().send(player, "&eRemoved &b" + removed.displayName() + " &efrom &6" + data.kind().displayName() + "&e.");
            plugin.gui().openMachine(player, data);
            return;
        }

        if (!event.isLeftClick()) return;
        if (!data.supportsHardware()) {
            plugin.msg().send(player, "&cThis machine cannot accept hardware.");
            return;
        }

        ItemStack cursor = event.getCursor();
        HardwareKind cursorHardware = hardwareFrom(cursor);
        if (cursorHardware != null) {
            installSpecificStack(player, data, slotCategory, cursorHardware, cursor, true);
            return;
        }

        ItemStack hand = player.getInventory().getItemInMainHand();
        HardwareKind handHardware = hardwareFrom(hand);
        if (handHardware != null) {
            installSpecificStack(player, data, slotCategory, handHardware, hand, false);
            player.getInventory().setItemInMainHand(hand.getAmount() <= 0 ? new ItemStack(Material.AIR) : hand);
            return;
        }

        int foundSlot = findHardwareInInventory(player, slotCategory);
        if (foundSlot >= 0) {
            ItemStack found = player.getInventory().getItem(foundSlot);
            HardwareKind hardware = hardwareFrom(found);
            if (hardware != null && data.install(hardware)) {
                takeOne(found);
                player.getInventory().setItem(foundSlot, found.getAmount() <= 0 ? new ItemStack(Material.AIR) : found);
                plugin.data().markDirty();
                plugin.msg().send(player, "&aInstalled &b" + hardware.displayName() + " &aon &e" + data.kind().displayName() + "&a.");
                plugin.gui().openMachine(player, data);
            }
            return;
        }

        plugin.msg().send(player, "&eLeft-click this slot while holding or carrying a &b" + nice(slotCategory.name()) + " &epart. Right-click removes one.");
    }

    private void installSpecificStack(Player player, MachineData data, HardwareCategory slotCategory, HardwareKind hardware, ItemStack stack, boolean cursor) {
        if (hardware.isRepairItem()) {
            plugin.msg().send(player, "&cRepair boxes are used by right-clicking the placed machine, not by installing them.");
            return;
        }
        if (hardware.category() != slotCategory) {
            plugin.msg().send(player, "&cThat is &e" + nice(hardware.category().name()) + "&c hardware, not &e" + nice(slotCategory.name()) + "&c.");
            return;
        }
        if (!data.canInstall(hardware)) {
            plugin.msg().send(player, "&cNo available &e" + nice(slotCategory.name()) + " &cslot on this machine.");
            return;
        }

        data.install(hardware);
        takeOne(stack);
        if (cursor) player.setItemOnCursor(stack.getAmount() <= 0 ? new ItemStack(Material.AIR) : stack);
        plugin.data().markDirty();
        plugin.msg().send(player, "&aInstalled &b" + hardware.displayName() + " &aon &e" + data.kind().displayName() + "&a.");
        plugin.gui().openMachine(player, data);
    }

    private int findHardwareInInventory(Player player, HardwareCategory category) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            HardwareKind hardware = hardwareFrom(contents[i]);
            if (hardware != null && !hardware.isRepairItem() && hardware.category() == category) return i;
        }
        return -1;
    }

    private HardwareKind hardwareFrom(ItemStack item) {
        if (item == null || item.getType().isAir()) return null;
        if (!"hardware".equalsIgnoreCase(plugin.items().readMegaType(item))) return null;
        String id = plugin.items().readMegaId(item);
        return HardwareKind.byId(id == null ? "" : id);
    }

    private void takeOne(ItemStack stack) {
        stack.setAmount(Math.max(0, stack.getAmount() - 1));
    }

    private HardwareCategory hardwareCategoryForSlot(int slot) {
        return switch (slot) {
            case 19 -> HardwareCategory.GPU;
            case 20 -> HardwareCategory.CPU;
            case 21 -> HardwareCategory.RAM;
            case 22 -> HardwareCategory.MOTHERBOARD;
            case 23 -> HardwareCategory.PSU;
            case 24 -> HardwareCategory.STORAGE;
            case 25 -> HardwareCategory.NETWORK;
            case 26 -> HardwareCategory.COOLING_PART;
            case 34 -> HardwareCategory.MODULE;
            default -> null;
        };
    }


    private boolean ensureCode(Player player) {
        PlayerData data = plugin.data().player(player.getUniqueId(), player.getName());
        if (data.hasBitcoinCode()) return true;
        plugin.msg().send(player, "&cBefore buying mining gear, set your private fake BTC code with &e/code set <code>&c. Remember it!");
        return false;
    }

    private boolean requiresCodeForPurchase(HardwareKind kind) {
        return switch (kind.category()) {
            case GPU, CPU, RAM, MOTHERBOARD, PSU, STORAGE, NETWORK, MODULE, COOLING_PART, MATERIAL -> true;
            case TOOL -> kind.isBitcoinUsb();
            case CURRENCY -> false;
        };
    }

    private boolean withdrawCost(Player player, double cost) {
        if (!checkEconomy(player, cost)) return false;
        if (cost <= 0) return true;
        if (!plugin.economy().withdraw(player, cost)) {
            plugin.msg().send(player, "&cVault withdrawal failed. No item was given and no upgrade was applied.");
            return false;
        }
        return true;
    }

    private boolean checkEconomy(Player player, double cost) {
        if (cost <= 0) return true;
        if (!plugin.economy().isReady()) {
            plugin.msg().send(player, "&cVault economy is not hooked. Install Vault + an economy plugin like EssentialsX Economy.");
            return false;
        }
        if (!plugin.economy().has(player, cost)) {
            plugin.msg().send(player, "&cNot enough money. Need &e" + plugin.msg().money(cost) + "&c. You have &e" + plugin.msg().money(plugin.economy().balance(player)) + "&c.");
            return false;
        }
        return true;
    }

    private String nice(String raw) {
        String lower = raw.toLowerCase().replace('_', ' ');
        StringBuilder out = new StringBuilder();
        for (String part : lower.split(" ")) {
            if (part.isEmpty()) continue;
            if (out.length() > 0) out.append(' ');
            out.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return out.toString();
    }

    private boolean isBack(ItemStack item) {
        if (item.getType() != Material.ARROW || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        String name = ChatColor.stripColor(meta.getDisplayName());
        return "Back".equalsIgnoreCase(name);
    }

    private String hiddenLoc(ItemStack item) {
        if (!item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return null;
        for (String line : meta.getLore()) {
            String stripped = ChatColor.stripColor(line);
            if (stripped != null && stripped.startsWith("LOC=")) return stripped.substring(4);
        }
        return null;
    }

    private String hiddenArea(ItemStack item) {
        if (!item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return null;
        for (String line : meta.getLore()) {
            String stripped = ChatColor.stripColor(line);
            if (stripped != null && stripped.startsWith("AREA=")) return stripped.substring(5);
        }
        return null;
    }

    private String areaOrNull(String area) {
        if (area == null || area.isBlank() || "all".equalsIgnoreCase(area)) return null;
        return plugin.areas().sanitize(area);
    }

    private String hiddenTarget(ItemStack item) {
        if (!item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return null;
        for (String line : meta.getLore()) {
            String stripped = ChatColor.stripColor(line);
            if (stripped != null && stripped.startsWith("TARGET=")) return stripped.substring(7);
        }
        return null;
    }

    private String symbolFromName(ItemStack item) {
        if (!item.hasItemMeta()) return null;
        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        if (name == null || name.isBlank()) return null;
        String[] parts = name.split(" ");
        return parts.length == 0 ? null : parts[0].toUpperCase();
    }
}
