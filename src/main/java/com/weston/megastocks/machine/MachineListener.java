package com.weston.megastocks.machine;

import com.weston.megastocks.MegaStocksPlugin;
import com.weston.megastocks.data.PlayerData;
import com.weston.megastocks.item.HardwareKind;
import com.weston.megastocks.util.LocationKey;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public final class MachineListener implements Listener {
    private final MegaStocksPlugin plugin;
    public MachineListener(MegaStocksPlugin plugin) { this.plugin = plugin; }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        ItemStack hand = event.getItemInHand();
        String type = plugin.items().readMegaType(hand);
        if (!"machine".equalsIgnoreCase(type)) return;
        MachineKind kind = MachineKind.byId(plugin.items().readMegaId(hand));
        if (kind == null) return;
        Player player = event.getPlayer();
        if (!plugin.machines().canPlace(player)) { event.setCancelled(true); plugin.msg().send(player, "&cMachine limit reached. Upgrade config or remove old machines."); return; }
        MachineData placed = plugin.machines().register(event.getBlockPlaced().getLocation(), player, kind);
        plugin.msg().send(player, "&aPlaced &e" + kind.displayName() + "&a in area &b" + plugin.areas().displayName(placed.areaId()) + "&a. Right-click it to manage it.");
        plugin.msg().send(player, "&7Tip: look at this machine and use &e/mega pickup &7to safely pick it up with all installed parts and stored fake BTC.");
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        MachineData data = plugin.machines().at(event.getBlock());
        if (data == null) return;
        event.setDropItems(false);
        if (!plugin.machines().canPickupOrSteal(event.getPlayer(), data, true)) {
            event.setCancelled(true);
            return;
        }
        plugin.machines().pickupMachine(event.getPlayer(), data, true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) return;
        MachineData data = plugin.machines().at(event.getClickedBlock());
        if (data == null) return;
        event.setCancelled(true);
        Player player = event.getPlayer();
        if (!data.owner().equals(player.getUniqueId()) && !player.hasPermission("megastocks.admin")) { plugin.msg().send(player, "&cThat machine belongs to &e" + ChatColor.stripColor(data.ownerName()) + "&c."); return; }
        HardwareKind repair = hardwareInHand(player.getInventory().getItemInMainHand());
        if (repair != null && repair.isRepairItem()) { useRepairItem(player, data, player.getInventory().getItemInMainHand(), repair); return; }
        switch (data.kind()) {
            case RESOURCE_MONITOR_STATION, NETWORK_OPERATIONS_CENTER, AREA_MONITOR_STATION -> plugin.gui().openMonitor(player, data.areaId());
            case AREA_CONTROL_TERMINAL -> plugin.gui().openAreaList(player);
            case STOCK_TRADING_STATION -> plugin.gui().openMarket(player);
            case ATM_STATION -> plugin.gui().openWallet(player);
            case SELL_STATION -> { double collected = plugin.machines().collectAllBtc(player); double sold = plugin.machines().sellAllBtc(player); if (sold < 0) plugin.msg().send(player, "&cVault economy is not ready, so BTC could not be sold."); else plugin.msg().send(player, "&aCollected &b" + plugin.msg().btc(collected) + " &aand sold all wallet BTC for &e" + plugin.msg().money(sold) + "&a."); }
            case HARDWARE_BENCH -> plugin.gui().openHardwareShop(player);
            case POWER_MANAGEMENT_STATION -> plugin.gui().openPower(player, data.areaId());
            case MINING_CONTROL_STATION -> plugin.gui().openMiningControl(player, data.areaId());
            case UPGRADE_STATION, REPAIR_STATION -> plugin.gui().openMachineList(player);
            case BLACK_MARKET_STATION -> plugin.gui().openShop(player);
            default -> plugin.gui().openMachine(player, data);
        }
    }
    private HardwareKind hardwareInHand(ItemStack item) { String type = plugin.items().readMegaType(item); if (!"hardware".equalsIgnoreCase(type)) return null; return HardwareKind.byId(plugin.items().readMegaId(item)); }
    private void useRepairItem(Player player, MachineData data, ItemStack hand, HardwareKind repair) {
        double before = data.heat();
        data.heat(Math.max(0.0, data.heat() - repair.repairAmount()));
        if (data.heat() < plugin.getConfig().getDouble("machine.overheat-at", 100.0)) data.enabled(true);
        if (hand.getAmount() <= 1) player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        else hand.setAmount(hand.getAmount() - 1);
        plugin.data().markDirty();
        plugin.msg().send(player, "&aUsed &e" + repair.displayName() + "&a on &6" + data.kind().displayName() + "&a. Heat: &c" + String.format("%.1f", before) + " &7-> &a" + String.format("%.1f", data.heat()));
    }
}
