package com.agent1k.deathenchants.listener;

import com.agent1k.deathenchants.DeathEnchants;
import com.agent1k.deathenchants.enchant.Enchants;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.enchantment.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class Interfaces implements Listener {
    private final DeathEnchants plugin;
    public Interfaces(DeathEnchants plugin) { this.plugin = plugin; }
    private static final class Menu extends com.agent1k.deathenchants.menu.AbstractMenu {
        final ItemStack original;
        final int slot;
        final Location table;
        final Map<Integer, Enchants.Definition> offers = new HashMap<>();
        final Map<Integer, Integer> levels = new HashMap<>();
        Menu(Player p, Location table) { original = p.getInventory().getItemInMainHand().clone(); slot = p.getInventory().getHeldItemSlot(); this.table = table; }
    }
    @EventHandler(ignoreCancelled = true)
    public void open(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND || e.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK || e.getClickedBlock().getType() != Material.ENCHANTING_TABLE || !e.getPlayer().isSneaking()) return;
        e.setCancelled(true);
        Player p = e.getPlayer();
        if (p.getInventory().getItemInMainHand().getType().isAir() || p.getInventory().getItemInMainHand().getAmount() != 1) return;
        Menu menu = new Menu(p, e.getClickedBlock().getLocation());
        menu.inventory = Bukkit.createInventory(menu, 9, "DeathEnchants");
        var costs = plugin.getConfig().getIntegerList("table.levels");
        for (int i = 0; i < Math.min(3, costs.size()); i++) {
            var d = plugin.enchants.random(menu.original.getType());
            if (d == null) continue;
            int level = Math.min(d.max(), i + 1);
            int slot = 2 + i * 2;
            ItemStack icon = plugin.enchants.book(d.id(), level);
            var meta = icon.getItemMeta(); meta.setDisplayName("§e" + costs.get(i) + " лвл — " + d.name() + " " + Enchants.roman(level)); icon.setItemMeta(meta);
            menu.offers.put(slot, d); menu.levels.put(slot, level); menu.inventory.setItem(slot, icon);
        }
        p.openInventory(menu.inventory);
    }
    @EventHandler
    public void click(InventoryClickEvent e) {
        if (!(e.getView().getTopInventory().getHolder() instanceof Menu menu)) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player p) || !menu.offers.containsKey(e.getRawSlot())) return;
        if (!p.getWorld().equals(menu.table.getWorld()) || p.getLocation().distanceSquared(menu.table) > 36 || menu.table.getBlock().getType() != Material.ENCHANTING_TABLE) { p.closeInventory(); return; }
        if (!menu.original.equals(p.getInventory().getItem(menu.slot))) { p.closeInventory(); return; }
        int index = (e.getRawSlot() - 2) / 2;
        var costs = plugin.getConfig().getIntegerList("table.levels");
        if (index >= costs.size()) { p.closeInventory(); return; }
        int cost = Math.max(1, costs.get(index));
        if (p.getGameMode() != GameMode.CREATIVE && (p.getLevel() < cost || !p.getInventory().containsAtLeast(new ItemStack(Material.LAPIS_LAZULI), index + 1))) return;
        var d = menu.offers.get(e.getRawSlot());
        if (plugin.enchants.get(d.id()) == null) { p.closeInventory(); return; }
        ItemStack result = menu.original.clone();
        if (result.getType() == Material.BOOK) result.setType(Material.ENCHANTED_BOOK);
        plugin.enchants.apply(result, d.id(), menu.levels.get(e.getRawSlot()));
        if (p.getGameMode() != GameMode.CREATIVE) { p.giveExpLevels(-cost); p.getInventory().removeItem(new ItemStack(Material.LAPIS_LAZULI, index + 1)); }
        p.getInventory().setItem(menu.slot, result);
        p.closeInventory();
        p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
    }
    @EventHandler
    public void drag(InventoryDragEvent e) { if (e.getView().getTopInventory().getHolder() instanceof Menu) e.setCancelled(true); }
    @EventHandler(ignoreCancelled = true)
    public void enchant(EnchantItemEvent e) {
        for (var entry : e.getEnchantsToAdd().entrySet()) {
            String path = "vanilla-levels." + entry.getKey().getKey().getKey();
            if (!plugin.getConfig().contains(path)) continue;
            int max = plugin.getConfig().getInt(path, entry.getKey().getMaxLevel());
            int level = Math.max(1, Math.min(255, max));
            entry.setValue(level);
        }
        if (!plugin.enchants.roll(plugin.getConfig().getDouble("table.chance",30))) return;
        var d = plugin.enchants.random(e.getItem().getType());
        if (d != null) plugin.enchants.apply(e.getItem(), d.id(), ThreadLocalRandom.current().nextInt(d.max()) + 1);
    }
    @EventHandler(ignoreCancelled = true)
    public void loot(LootGenerateEvent e) {
        var tables = plugin.getConfig().getStringList("loot.tables");
        if (!tables.isEmpty() && !tables.contains(e.getLootTable().getKey().toString())) return;
        if (!plugin.enchants.roll(plugin.getConfig().getDouble("loot.chance",12))) return;
        var d = plugin.enchants.random(Material.BOOK);
        if (d != null) e.getLoot().add(plugin.enchants.book(d.id(), ThreadLocalRandom.current().nextInt(d.max()) + 1));
    }
    @EventHandler
    public void anvil(PrepareAnvilEvent e) {
        ItemStack left = e.getInventory().getItem(0), right = e.getInventory().getItem(1);
        if (left == null || right == null) return;
        ItemStack result = e.getResult() == null ? left.clone() : e.getResult().clone();
        boolean changed = false;
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
        }
    }
    @EventHandler
    public void grind(PrepareGrindstoneEvent e) {
        ItemStack input = e.getInventory().getItem(0);
        if (input == null) input = e.getInventory().getItem(1);
        if (input == null) return;
        boolean custom = false;
        for (var d : plugin.enchants.all()) if (plugin.enchants.level(input, d.id()) > 0) custom = true;
        if (!custom) return;
        ItemStack result = plugin.enchants.clear(e.getResult() == null ? input : e.getResult());
        for (Enchantment enchantment : new HashSet<>(result.getEnchantments().keySet())) if (!enchantment.isCursed()) result.removeEnchantment(enchantment);
        if (result.getItemMeta() instanceof org.bukkit.inventory.meta.EnchantmentStorageMeta meta) {
            for (Enchantment enchantment : new HashSet<>(meta.getStoredEnchants().keySet())) if (!enchantment.isCursed()) meta.removeStoredEnchant(enchantment);
            result.setItemMeta(meta);
            if (meta.getStoredEnchants().isEmpty()) result.setType(Material.BOOK);
        }
        e.setResult(result);
    }
}
