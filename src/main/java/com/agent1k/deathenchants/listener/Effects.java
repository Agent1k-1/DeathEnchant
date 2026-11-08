package com.agent1k.deathenchants.listener;

import com.agent1k.deathenchants.DeathEnchants;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.Damageable;
import java.util.*;

public final class Effects implements Listener {
    private final DeathEnchants plugin;
    private final Set<UUID> breaking = new HashSet<>();
    public Effects(DeathEnchants plugin) { this.plugin = plugin; }
    @EventHandler(ignoreCancelled = true)
    public void damage(PlayerItemDamageEvent e) {
        ItemStack item = e.getItem();
        if (plugin.enchants.level(item, "unbreakable") > 0) { e.setCancelled(true); return; }
        if (plugin.enchants.level(item, "pinger") == 0 || !(item.getItemMeta() instanceof Damageable meta)) return;
        int max = item.getType().getMaxDurability();
        int remaining = max - meta.getDamage() - e.getDamage();
        double threshold = max * plugin.getConfig().getDouble("pinger.remaining-percent", 10) / 100;
        if (remaining <= threshold && max - meta.getDamage() > threshold) plugin.actions.run(e.getPlayer(), "pinger", Map.of("durability", String.valueOf(remaining)));
    }
    @EventHandler(ignoreCancelled = true)
    public void hit(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player p)) return;
        if (plugin.enchants.roll(plugin.enchants.value(p.getInventory().getItemInMainHand(), "vampirism"))) p.setHealth(Math.min(p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue(), p.getHealth() + 2));
    }
    private double experience(Player p) {
        double value = plugin.enchants.value(p.getInventory().getItemInMainHand(), "experienced");
        value = Math.max(value, plugin.enchants.value(p.getInventory().getItemInOffHand(), "experienced"));
        for (var item : p.getInventory().getArmorContents()) value = Math.max(value, plugin.enchants.value(item, "experienced"));
        return value;
    }
    @EventHandler
    public void death(EntityDeathEvent e) {
        Player p = e.getEntity().getKiller();
        if (p != null) e.setDroppedExp((int) Math.round(e.getDroppedExp() * (1 + experience(p) / 100)));
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void mine(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (p.getGameMode() == GameMode.CREATIVE) return;
        ItemStack tool = p.getInventory().getItemInMainHand();
        e.setExpToDrop((int) Math.round(e.getExpToDrop() * (1 + experience(p) / 100)));
        Block origin = e.getBlock();
        if (plugin.enchants.level(tool, "sowing") > 0 && origin.getBlockData() instanceof Ageable age && age.getAge() == age.getMaximumAge()) {
            Material seed = switch (origin.getType()) { case WHEAT -> Material.WHEAT_SEEDS; case CARROTS -> Material.CARROT; case POTATOES -> Material.POTATO; case BEETROOTS -> Material.BEETROOT_SEEDS; case NETHER_WART -> Material.NETHER_WART; default -> null; };
            if (seed != null && p.getInventory().contains(seed)) {
                Ageable data = (Ageable) age.clone();
                data.setAge(0);
                Bukkit.getScheduler().runTask(plugin, () -> { if (origin.getType().isAir() && p.getInventory().contains(seed)) { p.getInventory().removeItem(new ItemStack(seed)); origin.setBlockData(data); } });
            }
        }
        if (breaking.contains(p.getUniqueId())) return;
        List<Block> blocks = new ArrayList<>();
        if (plugin.enchants.level(tool, "lumberjack") > 0 && Tag.LOGS.isTagged(origin.getType())) {
            Set<Block> visited = new HashSet<>(); ArrayDeque<Block> queue = new ArrayDeque<>(); queue.add(origin); visited.add(origin);
            int limit = Math.max(1, Math.min(1024, plugin.getConfig().getInt("limits.tree-blocks", 128)));
            while (!queue.isEmpty() && visited.size() < limit) {
                Block b = queue.remove();
                for (int x = -1; x <= 1; x++) for (int y = 0; y <= 1; y++) for (int z = -1; z <= 1; z++) {
                    Block next = b.getRelative(x,y,z);
                    if (Tag.LOGS.isTagged(next.getType()) && visited.size() < limit && visited.add(next)) { queue.add(next); blocks.add(next); }
                }
            }
        } else if (plugin.enchants.roll(plugin.enchants.value(tool, "drill"))) {
            var direction = p.getLocation().getDirection();
            for (int a = -1; a <= 1; a++) for (int b = -1; b <= 1; b++) {
                if (a == 0 && b == 0) continue;
                Block next = Math.abs(direction.getY()) > 0.6 ? origin.getRelative(a,0,b) : Math.abs(direction.getX()) > Math.abs(direction.getZ()) ? origin.getRelative(0,a,b) : origin.getRelative(a,b,0);
                if (!next.getType().isAir() && next.getType().getHardness() >= 0 && !(next.getState() instanceof org.bukkit.inventory.InventoryHolder)) blocks.add(next);
            }
        }
        if (!blocks.isEmpty()) Bukkit.getScheduler().runTask(plugin, () -> {
            if (!origin.getType().isAir() || !p.isOnline()) return;
            breaking.add(p.getUniqueId());
            try { for (Block block : blocks) { if (p.getInventory().getItemInMainHand().getType() != tool.getType()) break; p.breakBlock(block); } } finally { breaking.remove(p.getUniqueId()); }
        });
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void drops(BlockDropItemEvent e) {
        ItemStack tool = e.getPlayer().getInventory().getItemInMainHand();
        boolean smelt = plugin.enchants.level(tool, "smelting") > 0;
        boolean teleport = plugin.enchants.level(tool, "telepathy") > 0;
        for (Item entity : new ArrayList<>(e.getItems())) {
            ItemStack item = entity.getItemStack();
            if (smelt) {
                var recipes = Bukkit.recipeIterator();
                while (recipes.hasNext()) {
                    Recipe recipe = recipes.next();
                    if (recipe instanceof FurnaceRecipe furnace && furnace.getInputChoice().test(item)) { ItemStack result = furnace.getResult().clone(); result.setAmount(result.getAmount() * item.getAmount()); item = result; break; }
                }
            }
            if (teleport) {
                var left = e.getPlayer().getInventory().addItem(item);
                if (left.isEmpty()) { e.getItems().remove(entity); entity.remove(); }
                else entity.setItemStack(left.values().iterator().next());
            } else entity.setItemStack(item);
        }
    }
    @EventHandler(ignoreCancelled = true)
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
