package com.agent1k.anvil;

import com.agent1k.DeathEnchants;
import org.bukkit.enchantments.Enchantment;

public final class EnchantmentLevelPolicy {
    private final DeathEnchants plugin;
    public EnchantmentLevelPolicy(DeathEnchants plugin) { this.plugin = plugin; }
    public int maximum(Enchantment enchantment) {
        return Math.max(1, Math.min(255, plugin.getConfig().getInt("vanilla-levels." + enchantment.getKey().getKey(), enchantment.getMaxLevel())));
    }
    public int merge(Enchantment enchantment, int current, int incoming) {
        int desired = current == incoming ? current + 1 : Math.max(current, incoming);
        return Math.max(current, Math.min(maximum(enchantment), desired));
    }
}
