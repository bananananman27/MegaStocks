package com.weston.megastocks.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class MenuHolder implements InventoryHolder {
    private final MenuType type;
    private final String data;
    private Inventory inventory;

    public MenuHolder(MenuType type, String data) {
        this.type = type;
        this.data = data;
    }

    public MenuType type() { return type; }
    public String data() { return data; }

    public void inventory(Inventory inventory) { this.inventory = inventory; }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
