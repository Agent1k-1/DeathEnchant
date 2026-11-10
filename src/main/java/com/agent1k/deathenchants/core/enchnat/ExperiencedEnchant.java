package com.agent1k.deathenchants.core.enchnat;

import com.agent1k.deathenchants.DeathEnchants;
import org.bukkit.entity.Player;

public final class ExperiencedEnchant {
    private final DeathEnchants plugin;
    public ExperiencedEnchant(DeathEnchants plugin) { this.plugin = plugin; }
    public double experience(Player p) {
        double value = plugin.enchants.value(p.getInventory().getItemInMainHand(), "experienced");
        value = Math.max(value, plugin.enchants.value(p.getInventory().getItemInOffHand(), "experienced"));
        for (var item : p.getInventory().getArmorContents()) value = Math.max(value, plugin.enchants.value(item, "experienced"));
        return value;
    }
}
