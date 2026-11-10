package com.agent1k.deathenchants.anvil;

import com.agent1k.deathenchants.DeathEnchants;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

public final class AnvilListener implements Listener {
    private final DeathEnchants plugin;
    private final VanillaEnchantMerger merger;
    public AnvilListener(DeathEnchants plugin) {
        this.plugin = plugin;
        this.merger = new VanillaEnchantMerger(new EnchantmentLevelPolicy(plugin));
    }
    @EventHandler
    public void anvil(PrepareAnvilEvent e) {
        ItemStack left = e.getInventory().getItem(0), right = e.getInventory().getItem(1);
        if (left == null || right == null || left.getAmount() != 1 || right.getAmount() != 1) return;
        ItemStack result = e.getResult() == null ? left.clone() : e.getResult().clone();
        boolean changed = merger.merge(left, right, result);
        for (var d : plugin.enchants.all()) {
            int incoming = plugin.enchants.level(right, d.id());
            if (incoming == 0 || !(result.getType() == Material.ENCHANTED_BOOK || d.target().accepts(result.getType()))) continue;
            if (right.getType() != Material.ENCHANTED_BOOK && right.getType() != left.getType()) continue;
            int old = plugin.enchants.level(left, d.id());
            plugin.enchants.apply(result, d.id(), Math.min(d.max(), old == incoming ? old + 1 : Math.max(old, incoming)));
            changed = true;
        }
        if (changed) {
            String name = e.getInventory().getRenameText();
            if (name != null && !name.isBlank()) { var meta = result.getItemMeta(); meta.setDisplayName(name); result.setItemMeta(meta); }
            result.setAmount(1); e.setResult(result); e.getInventory().setRepairCost(Math.max(1, e.getInventory().getRepairCost()));
            e.getInventory().setRepairCostAmount(1);
        }
    }
}
