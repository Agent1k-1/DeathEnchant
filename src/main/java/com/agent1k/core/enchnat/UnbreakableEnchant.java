package com.agent1k.core.enchnat;

import com.agent1k.DeathEnchants;
import org.bukkit.event.player.PlayerItemDamageEvent;

public final class UnbreakableEnchant {
    private final DeathEnchants plugin;
    public UnbreakableEnchant(DeathEnchants plugin) { this.plugin = plugin; }
    public void damage(PlayerItemDamageEvent e) {
        if (plugin.enchants.level(e.getItem(), "unbreakable") > 0) e.setCancelled(true);
    }
}
