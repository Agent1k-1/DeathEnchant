package com.agent1k.core.enchnat;

import com.agent1k.DeathEnchants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.block.EntityBlockFormEvent;

public final class LavawalkerEnchant {
    private final DeathEnchants plugin;
    public LavawalkerEnchant(DeathEnchants plugin) { this.plugin = plugin; }
    public void move(PlayerMoveEvent e) {
        if (e.getTo() == null || e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockY() == e.getTo().getBlockY() && e.getFrom().getBlockZ() == e.getTo().getBlockZ()) return;
        Player p = e.getPlayer();
        if (plugin.enchants.level(p.getInventory().getBoots(), "lavawalker") == 0) return;
        Block center = e.getTo().getBlock().getRelative(0,-1,0);
        for (int x = -1; x <= 1; x++) for (int z = -1; z <= 1; z++) {
            Block b = center.getRelative(x,0,z);
            if (b.getType() != Material.LAVA) continue;
            var old = b.getBlockData().clone();
            var state = b.getState();
            state.setType(Material.OBSIDIAN);
            var event = new EntityBlockFormEvent(p, b, state);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) continue;
            b.setType(Material.OBSIDIAN);
            Bukkit.getScheduler().runTaskLater(plugin, () -> { if (b.getType() == Material.OBSIDIAN) b.setBlockData(old); }, Math.max(1, plugin.getConfig().getLong("lava.duration-ticks",100)));
        }
    }
}
