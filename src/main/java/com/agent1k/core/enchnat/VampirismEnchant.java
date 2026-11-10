package com.agent1k.core.enchnat;

import com.agent1k.DeathEnchants;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class VampirismEnchant {
    private final DeathEnchants plugin;
    public VampirismEnchant(DeathEnchants plugin) { this.plugin = plugin; }
    public void hit(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player p)) return;
        if (plugin.enchants.roll(plugin.enchants.value(p.getInventory().getItemInMainHand(), "vampirism"))) p.setHealth(Math.min(p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue(), p.getHealth() + 2));
    }
}
