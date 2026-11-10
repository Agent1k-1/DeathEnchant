package com.agent1k.deathenchants.listener;

import com.agent1k.deathenchants.DeathEnchants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public final class ItemRefresh implements Listener {
    private final DeathEnchants plugin;
    public ItemRefresh(DeathEnchants plugin) { this.plugin = plugin; }
    public void refresh(Player player) {
        if (!player.isOnline()) return;
        for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
            ItemStack item = player.getInventory().getItem(slot);
            if (!plugin.enchants.hasCustom(item)) continue;
            ItemStack updated = item.clone();
            plugin.enchants.render(updated);
            if (!updated.equals(item)) player.getInventory().setItem(slot, updated);
        }
        ItemStack cursor = player.getItemOnCursor();
        if (plugin.enchants.hasCustom(cursor)) {
            ItemStack updated = cursor.clone();
            plugin.enchants.render(updated);
            if (!updated.equals(cursor)) player.setItemOnCursor(updated);
        }
    }
    private void later(Player player) { Bukkit.getScheduler().runTask(plugin, () -> refresh(player)); }
    @EventHandler
    public void join(PlayerJoinEvent event) { later(event.getPlayer()); }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void click(InventoryClickEvent event) { if (event.getWhoClicked() instanceof Player player) later(player); }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void drag(InventoryDragEvent event) { if (event.getWhoClicked() instanceof Player player) later(player); }
    @EventHandler
    public void close(InventoryCloseEvent event) { if (event.getPlayer() instanceof Player player) later(player); }
}
