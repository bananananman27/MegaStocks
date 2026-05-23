package com.weston.megastocks.item;

import com.weston.megastocks.MegaStocksPlugin;
import com.weston.megastocks.data.PlayerData;
import com.weston.megastocks.machine.MachineKind;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public final class ItemFactory {
    private final MegaStocksPlugin plugin;
    private final NamespacedKey typeKey;
    private final NamespacedKey idKey;
    private final NamespacedKey codeOwnerKey;
    private final NamespacedKey codeBackupKey;

    public ItemFactory(MegaStocksPlugin plugin) {
        this.plugin = plugin;
        this.typeKey = new NamespacedKey(plugin, "mega_type");
        this.idKey = new NamespacedKey(plugin, "mega_id");
        this.codeOwnerKey = new NamespacedKey(plugin, "code_owner");
        this.codeBackupKey = new NamespacedKey(plugin, "code_backup");
    }

    public NamespacedKey typeKey() { return typeKey; }
    public NamespacedKey idKey() { return idKey; }
    public NamespacedKey codeOwnerKey() { return codeOwnerKey; }
    public NamespacedKey codeBackupKey() { return codeBackupKey; }

    public ItemStack machineItem(MachineKind kind, int amount) {
        ItemStack item = new ItemStack(kind.blockMaterial(), Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + kind.displayName());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + "MegaStocks Machine");
        lore.add(ChatColor.GRAY + "ID: " + ChatColor.WHITE + kind.id());
        lore.add(ChatColor.GRAY + "Category: " + ChatColor.WHITE + kind.category().name());
        lore.add(ChatColor.GRAY + "Cost: " + ChatColor.GREEN + plugin.msg().money(plugin.machinePrice(kind)));
        if (kind.powerProduction() > 0) lore.add(ChatColor.GRAY + "Power Output: " + ChatColor.YELLOW + String.format("%,.0fW", kind.powerProduction()));
        if (kind.powerUse() > 0) lore.add(ChatColor.GRAY + "Power Use: " + ChatColor.YELLOW + String.format("%,.0fW", kind.powerUse()));
        if (kind.heatPerTick() != 0) lore.add(ChatColor.GRAY + "Heat: " + ChatColor.RED + String.format("%,.0f", kind.heatPerTick()));
        if (kind.btcPerMinute() > 0) lore.add(ChatColor.GRAY + "BTC/min: " + ChatColor.AQUA + String.format("%.8f", kind.btcPerMinute()));
        if (kind.isWire()) {
            lore.add(ChatColor.GRAY + "Wire/Pipe: " + ChatColor.AQUA + "boosts area power/cooling automatically");
            lore.add(ChatColor.DARK_GRAY + "Easy mode: just place it in the right area.");
        }
        if (kind.isServerCooler()) lore.add(ChatColor.AQUA + "Huge area cooler: cools the whole area like CRAZY.");
        lore.add("");
        lore.add(ChatColor.YELLOW + "Place this block to create the machine.");
        lore.add(ChatColor.GRAY + "Pickup tip: look at it and run " + ChatColor.YELLOW + "/mega pickup" + ChatColor.GRAY + ".");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, "machine");
        meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, kind.id());
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack hardwareItem(HardwareKind kind, int amount) {
        ItemStack item = new ItemStack(kind.material(), Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + kind.displayName());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + "MegaStocks Hardware");
        lore.add(ChatColor.GRAY + "ID: " + ChatColor.WHITE + kind.id());
        lore.add(ChatColor.GRAY + "Category: " + ChatColor.WHITE + kind.category().name());
        if (plugin.hardwarePrice(kind) > 0) lore.add(ChatColor.GRAY + "Cost: " + ChatColor.GREEN + plugin.msg().money(plugin.hardwarePrice(kind)));
        if (kind.category() == HardwareCategory.PSU && kind.watts() < 0) lore.add(ChatColor.GRAY + "PSU Capacity: " + ChatColor.YELLOW + String.format("%,.0fW", Math.abs(kind.watts())));
        else if (kind.watts() != 0) lore.add(ChatColor.GRAY + "Watts: " + ChatColor.YELLOW + String.format("%,.0fW", kind.watts()));
        if (kind.performance() != 0) lore.add(ChatColor.GRAY + "Performance: " + ChatColor.LIGHT_PURPLE + String.format("%,.0f", kind.performance()));
        if (kind.heat() != 0) lore.add(ChatColor.GRAY + "Heat: " + ChatColor.RED + String.format("%,.0f", kind.heat()));
        lore.add(ChatColor.GRAY + kind.description());
        if (kind.isRepairItem()) {
            lore.add("");
            lore.add(ChatColor.YELLOW + "Right-click one of your placed machines to repair it.");
        }
        if (kind.isHackingTool()) {
            lore.add("");
            lore.add(ChatColor.DARK_PURPLE + "Hidden use: install into a rack/miner to raise hack attack power.");
        }
        if (kind.isSecurityTool()) {
            lore.add("");
            lore.add(ChatColor.AQUA + "Security use: install into a vault/rack/miner to protect fake BTC.");
        }
        if (kind.isModule()) {
            lore.add("");
            lore.add(ChatColor.LIGHT_PURPLE + "Module use: install into machines for special automation/overclocking.");
        }
        if (kind.isBitcoinUsb()) {
            lore.add("");
            lore.add(ChatColor.GOLD + "Stores a private fake BTC wallet code backup.");
            lore.add(ChatColor.GRAY + "Buy requires /code set <code> first.");
        }
        if (kind.isScannerTool()) {
            lore.add("");
            lore.add(ChatColor.YELLOW + "Utility scanner for monitors, machines, and diagnostics.");
        }
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, "hardware");
        meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, kind.id());
        item.setItemMeta(meta);
        return item;
    }


    public ItemStack bitcoinUsbItem(PlayerData data, int amount) {
        ItemStack item = hardwareItem(HardwareKind.BITCOIN_USB, amount);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.DARK_GRAY + "Private code backup is stored in item data.");
        lore.add(ChatColor.GRAY + "Owner: " + ChatColor.WHITE + data.name());
        String code = data.bitcoinCode();
        String masked = code.length() <= 4 ? "****" : "****" + code.substring(Math.max(0, code.length() - 4));
        lore.add(ChatColor.GRAY + "Masked Code: " + ChatColor.YELLOW + masked);
        lore.add(ChatColor.RED + "Do not share your /code with other players.");
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(codeOwnerKey, PersistentDataType.STRING, data.uuid().toString());
        meta.getPersistentDataContainer().set(codeBackupKey, PersistentDataType.STRING, data.bitcoinCode());
        item.setItemMeta(meta);
        return item;
    }

    public boolean isMegaItem(ItemStack item) {
        return readMegaType(item) != null;
    }

    public String readMegaType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer().get(typeKey, PersistentDataType.STRING);
    }

    public String readMegaId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer().get(idKey, PersistentDataType.STRING);
    }
}
