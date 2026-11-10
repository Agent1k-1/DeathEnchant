package com.agent1k.menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class AbstractMenu implements InventoryHolder {
    public Inventory inventory;
    public final Inventory getInventory() { return inventory; }
}
