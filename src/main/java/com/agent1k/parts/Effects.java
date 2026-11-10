package com.agent1k.parts;

import com.agent1k.DeathEnchants;
import com.agent1k.core.enchnat.*;
import com.agent1k.deathenchants.core.enchnat.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import java.util.*;

public final class Effects implements Listener {
    private final DeathEnchants plugin;
    private final Set<UUID> breaking = new HashSet<>();
    private final DrillEnchant drill;
    private final PingerEnchant pinger;
    private final UnbreakableEnchant unbreakable;
    private final ExperiencedEnchant experienced;
    private final VampirismEnchant vampirism;
    private final SmeltingEnchant smelting;
    private final TelepathyEnchant telepathy;
    private final LavawalkerEnchant lavawalker;
    private final SowingEnchant sowing;
    private final LumberjackEnchant lumberjack;
    public Effects(DeathEnchants plugin) {
        this.plugin = plugin;
        this.drill = new DrillEnchant();
        this.pinger = new PingerEnchant(plugin);
        this.unbreakable = new UnbreakableEnchant(plugin);
        this.experienced = new ExperiencedEnchant(plugin);
        this.vampirism = new VampirismEnchant(plugin);
        this.smelting = new SmeltingEnchant();
        this.telepathy = new TelepathyEnchant();
        this.lavawalker = new LavawalkerEnchant(plugin);
        this.sowing = new SowingEnchant(plugin);
        this.lumberjack = new LumberjackEnchant(plugin);
    }
    @EventHandler(ignoreCancelled = true)
    public void damage(PlayerItemDamageEvent e) {
        unbreakable.damage(e);
        if (!e.isCancelled()) pinger.damage(e);
    }
    @EventHandler(ignoreCancelled = true)
    public void hit(EntityDamageByEntityEvent e) { vampirism.hit(e); }
    private double experience(Player p) { return experienced.experience(p); }
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
        sowing.plant(p, tool, origin);
        if (breaking.contains(p.getUniqueId())) return;
        List<Block> blocks = new ArrayList<>();
        if (plugin.enchants.level(tool, "lumberjack") > 0 && Tag.LOGS.isTagged(origin.getType())) {
            blocks.addAll(lumberjack.blocks(origin));
        } else if (plugin.enchants.roll(plugin.enchants.value(tool, "drill"))) {
            blocks.addAll(drill.blocks(p, origin));
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
                item = smelting.smelt(item);
            }
            if (teleport) {
                telepathy.collect(e, entity, item);
            } else entity.setItemStack(item);
        }
    }
    @EventHandler(ignoreCancelled = true)
    public void move(PlayerMoveEvent e) { lavawalker.move(e); }
}
