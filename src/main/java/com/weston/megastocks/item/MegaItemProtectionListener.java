package com.weston.megastocks.item;

import com.weston.megastocks.MegaStocksPlugin;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Crafter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.BrewingStandFuelEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public final class MegaItemProtectionListener implements Listener {
    private final MegaStocksPlugin plugin;
    private final Set<String> protectedInventories = Set.of(
            "CRAFTING",
            "WORKBENCH",
            "CRAFTER",
            "SMITHING",
            "ANVIL",
            "GRINDSTONE",
            "FURNACE",
            "BLAST_FURNACE",
            "SMOKER",
            "BREWING",
            "LOOM",
            "CARTOGRAPHY",
            "STONECUTTER"
    );

    public MegaItemProtectionListener(MegaStocksPlugin plugin) {
        this.plugin = plugin;
    }

    private boolean enabled() {
        return plugin.getConfig().getBoolean("anti-cheat.protect-megastocks-items", true);
    }

    private boolean isMega(ItemStack item) {
        return enabled() && plugin.items().isMegaItem(item);
    }

    private boolean anyMega(ItemStack[] items) {
        if (!enabled() || items == null) return false;
        for (ItemStack item : items) if (plugin.items().isMegaItem(item)) return true;
        return false;
    }

    private boolean protectedInventory(Inventory inventory) {
        if (inventory == null) return false;
        InventoryType type = inventory.getType();
        return protectedInventories.contains(type.name());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onNonMachineMegaBlockPlace(BlockPlaceEvent event) {
        if (!enabled()) return;
        ItemStack hand = event.getItemInHand();
        String megaType = plugin.items().readMegaType(hand);
        if (megaType == null) return;
        if ("machine".equalsIgnoreCase(megaType)) return;
        event.setCancelled(true);
        plugin.msg().send(event.getPlayer(), "&cMegaStocks hardware/items cannot be placed as normal Minecraft blocks. Use the MegaStocks machines/menus instead.");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (anyMega(event.getInventory().getMatrix())) event.getInventory().setResult(new ItemStack(Material.AIR));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        if (anyMega(event.getInventory().getMatrix())) {
            event.setCancelled(true);
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        if (anyMega(event.getInventory().getContents())) event.setResult(new ItemStack(Material.AIR));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (anyMega(event.getInventory().getContents())) event.setResult(new ItemStack(Material.AIR));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPrepareGrindstone(PrepareGrindstoneEvent event) {
        if (anyMega(event.getInventory().getContents())) event.setResult(new ItemStack(Material.AIR));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        if (isMega(event.getSource())) {
            event.setCancelled(true);
            event.setResult(new ItemStack(Material.AIR));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        if (isMega(event.getFuel())) event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCrafterCraft(CrafterCraftEvent event) {
        if (!enabled()) return;
        boolean megaIngredient = false;
        BlockState state = event.getBlock().getState();
        if (state instanceof Crafter crafter) {
            megaIngredient = anyMega(crafter.getInventory().getContents());
        }
        if (megaIngredient || isMega(event.getResult())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (!enabled()) return;
        if (!protectedInventory(event.getDestination())) return;
        if (isMega(event.getItem())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryPickupItem(InventoryPickupItemEvent event) {
        if (!enabled()) return;
        if (!protectedInventory(event.getInventory())) return;
        if (isMega(event.getItem().getItemStack())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBrewingFuel(BrewingStandFuelEvent event) {
        if (isMega(event.getFuel())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBrew(BrewEvent event) {
        if (anyMega(event.getContents().getContents())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onProtectedInventoryClick(InventoryClickEvent event) {
        if (!enabled()) return;
        Inventory top = event.getView().getTopInventory();
        if (!protectedInventory(top)) return;

        boolean movingIntoTop = event.getRawSlot() < top.getSize();
        boolean shiftIntoTop = event.isShiftClick() && event.getClickedInventory() != null && event.getClickedInventory().equals(event.getView().getBottomInventory());
        ItemStack hotbarSwap = null;
        ItemStack offhandSwap = null;
        if (event.getWhoClicked() instanceof org.bukkit.entity.Player player) {
            int hotbarButton = event.getHotbarButton();
            if (hotbarButton >= 0) hotbarSwap = player.getInventory().getItem(hotbarButton);
            if (event.getClick() == ClickType.SWAP_OFFHAND) offhandSwap = player.getInventory().getItemInOffHand();
        }
        if ((movingIntoTop || shiftIntoTop) && (isMega(event.getCursor()) || isMega(event.getCurrentItem()) || isMega(hotbarSwap) || isMega(offhandSwap))) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof org.bukkit.entity.Player player) {
                plugin.msg().send(player, "&cMegaStocks items cannot be used in vanilla crafting, smithing, furnaces, brewing, anvils, or other conversion menus.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onProtectedInventoryDrag(InventoryDragEvent event) {
        if (!enabled()) return;
        Inventory top = event.getView().getTopInventory();
        if (!protectedInventory(top)) return;
        if (!isMega(event.getOldCursor())) return;
        for (int slot : event.getRawSlots()) {
            if (slot < top.getSize()) {
                event.setCancelled(true);
                if (event.getWhoClicked() instanceof org.bukkit.entity.Player player) {
                    plugin.msg().send(player, "&cMegaStocks items cannot be placed into vanilla conversion menus.");
                }
                return;
            }
        }
    }
}
