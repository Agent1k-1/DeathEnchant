package com.agent1k.core.enchnat;

import com.agent1k.DeathEnchants;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import java.util.Map;

public final class PingerEnchant {
    private final DeathEnchants plugin;
    public PingerEnchant(DeathEnchants plugin) { this.plugin = plugin; }
    public void damage(PlayerItemDamageEvent e) {
        ItemStack item = e.getItem();
        if (plugin.enchants.level(item, "pinger") == 0 || !(item.getItemMeta() instanceof Damageable meta)) return;
        int max = item.getType().getMaxDurability();
        int remaining = max - meta.getDamage() - e.getDamage();
        double threshold = max * plugin.getConfig().getDouble("pinger.remaining-percent", 10) / 100;
        if (remaining <= threshold && max - meta.getDamage() > threshold) plugin.actions.run(e.getPlayer(), "pinger", Map.of("durability", String.valueOf(remaining)));
    }
}
