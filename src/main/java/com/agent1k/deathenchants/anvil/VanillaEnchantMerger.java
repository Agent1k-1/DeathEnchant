package com.agent1k.deathenchants.anvil;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import java.util.Map;

public final class VanillaEnchantMerger {
    private final EnchantmentLevelPolicy policy;
    public VanillaEnchantMerger(EnchantmentLevelPolicy policy) { this.policy = policy; }
    private Map<Enchantment, Integer> enchantments(ItemStack item) {
        return item.getItemMeta() instanceof EnchantmentStorageMeta meta ? meta.getStoredEnchants() : item.getEnchantments();
    }
    public boolean merge(ItemStack left, ItemStack right, ItemStack result) {
        if (right.getType() != Material.ENCHANTED_BOOK && right.getType() != left.getType()) return false;
        boolean changed = false;
        Map<Enchantment, Integer> original = enchantments(left);
        for (var entry : enchantments(right).entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (result.getType() != Material.ENCHANTED_BOOK && !enchantment.canEnchantItem(result)) continue;
            boolean conflict = enchantments(result).keySet().stream().anyMatch(other -> !other.equals(enchantment) && (other.conflictsWith(enchantment) || enchantment.conflictsWith(other)));
            if (conflict) continue;
            int current = original.getOrDefault(enchantment, 0);
            int level = policy.merge(enchantment, current, entry.getValue());
            if (level <= current || level <= enchantments(result).getOrDefault(enchantment, 0)) continue;
            if (result.getItemMeta() instanceof EnchantmentStorageMeta meta) {
                meta.addStoredEnchant(enchantment, level, true);
                result.setItemMeta(meta);
            } else result.addUnsafeEnchantment(enchantment, level);
            changed = true;
        }
        return changed;
    }
}
