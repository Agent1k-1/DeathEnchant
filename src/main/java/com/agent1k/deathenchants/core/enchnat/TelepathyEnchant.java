package com.agent1k.deathenchants.core.enchnat;

import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public final class TelepathyEnchant {
    public void collect(BlockDropItemEvent e, Item entity, ItemStack item) {
        var left = e.getPlayer().getInventory().addItem(item);
        if (left.isEmpty()) { e.getItems().remove(entity); entity.remove(); }
        else entity.setItemStack(left.values().iterator().next());
    }
}
