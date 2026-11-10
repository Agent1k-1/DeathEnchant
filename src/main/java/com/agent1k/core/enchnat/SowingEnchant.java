package com.agent1k.core.enchnat;

import com.agent1k.DeathEnchants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class SowingEnchant {
    private final DeathEnchants plugin;
    public SowingEnchant(DeathEnchants plugin) { this.plugin = plugin; }
    public void plant(Player p, ItemStack tool, Block origin) {
        if (plugin.enchants.level(tool, "sowing") > 0 && origin.getBlockData() instanceof Ageable age && age.getAge() == age.getMaximumAge()) {
            Material seed = switch (origin.getType()) { case WHEAT -> Material.WHEAT_SEEDS; case CARROTS -> Material.CARROT; case POTATOES -> Material.POTATO; case BEETROOTS -> Material.BEETROOT_SEEDS; case NETHER_WART -> Material.NETHER_WART; default -> null; };
            if (seed != null && p.getInventory().contains(seed)) {
                Ageable data = (Ageable) age.clone();
                data.setAge(0);
                Bukkit.getScheduler().runTask(plugin, () -> { if (origin.getType().isAir() && p.getInventory().contains(seed)) { p.getInventory().removeItem(new ItemStack(seed)); origin.setBlockData(data); } });
            }
        }
    }
}
