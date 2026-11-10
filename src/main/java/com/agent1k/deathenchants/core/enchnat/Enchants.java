package com.agent1k.deathenchants.core.enchnat;

import com.agent1k.deathenchants.core.inst.Target;

import com.agent1k.deathenchants.DeathEnchants;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataType;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class Enchants {
    public record Definition(String id, String name, int max, int weight, double chance, Target target, List<Double> values) { }
    private final DeathEnchants plugin;
    private final Map<String, Definition> definitions;
    private final Map<String, String> names;

    public Enchants(DeathEnchants plugin, Map<String, Definition> definitions, Map<String, String> names) {
        this.plugin = plugin;
        this.definitions = new LinkedHashMap<>(definitions);
        this.names = new LinkedHashMap<>(names);
    }
    public Collection<Definition> all() { return definitions.values(); }
    public Definition get(String id) { return definitions.get(id); }
    private NamespacedKey key(String id) { return new NamespacedKey(plugin, id); }
    public int level(ItemStack item, String id) {
        if (item == null || !item.hasItemMeta() || !definitions.containsKey(id)) return 0;
        if (id.equals("pinger") && item.getType() != Material.ENCHANTED_BOOK && !Target.TOOLS.accepts(item.getType())) return 0;
        return Math.max(0, item.getItemMeta().getPersistentDataContainer().getOrDefault(key(id), PersistentDataType.INTEGER, 0));
    }
    public double value(ItemStack item, String id) {
        int level = level(item, id);
        Definition d = get(id);
        return level == 0 || d.values().isEmpty() ? 0 : d.values().get(Math.min(level, d.values().size()) - 1);
    }
    public boolean roll(double chance) { return ThreadLocalRandom.current().nextDouble(100) < chance; }
    public Definition random(Material type) {
        var choices = all().stream().filter(d -> d.weight() > 0 && d.chance() > 0 && (type == Material.BOOK || type == Material.ENCHANTED_BOOK || d.target().accepts(type))).toList();
        int total = choices.stream().mapToInt(Definition::weight).sum();
        if (total == 0) return null;
        int n = ThreadLocalRandom.current().nextInt(total);
        for (var d : choices) { n -= d.weight(); if (n < 0) return roll(d.chance()) ? d : null; }
        return null;
    }
    public ItemStack book(String id, int level) { ItemStack book = new ItemStack(Material.ENCHANTED_BOOK); apply(book, id, level); return book; }
    public void apply(ItemStack item, String id, int level) {
        Definition d = get(id);
        if (d == null) return;
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key(id), PersistentDataType.INTEGER, Math.min(d.max(), Math.max(1, level)));
        item.setItemMeta(meta);
        render(item);
    }
    public void render(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        String marker = "§0§0§r";
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        removeLore(meta, lore);
        List<String> generated = new ArrayList<>();
        for (var d : all()) {
            int level = level(item, d.id());
            if (level == 0) continue;
            generated.add(marker + d.name() + " " + roman(level));
        }
        lore.addAll(generated);
        meta.getPersistentDataContainer().set(key("generated_lore"), PersistentDataType.STRING, String.join("\n", generated));
        meta.setLore(lore.isEmpty() ? null : lore);
        item.setItemMeta(meta);
    }
    public ItemStack clear(ItemStack source, ItemStack... inputs) {
        ItemStack item = source.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta.hasLore()) {
            var lore = new ArrayList<>(meta.getLore());
            removeLore(meta, lore);
            for (ItemStack input : inputs) if (input != null && input.hasItemMeta()) removeLore(input.getItemMeta(), lore);
            meta.setLore(lore.isEmpty() ? null : lore);
        }
        for (NamespacedKey key : new HashSet<>(meta.getPersistentDataContainer().getKeys())) if (key.getNamespace().equals(plugin.getName().toLowerCase(Locale.ROOT))) meta.getPersistentDataContainer().remove(key);
        item.setItemMeta(meta);
        return item;
    }
    public boolean hasCustom(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        for (NamespacedKey key : meta.getPersistentDataContainer().getKeys()) {
            if (key.getNamespace().equals(plugin.getName().toLowerCase(Locale.ROOT))) return true;
        }
        return meta.hasLore() && meta.getLore().stream().anyMatch(line -> line.startsWith("§0§0§r"));
    }
    private void removeLore(ItemMeta meta, List<String> lore) {
        Set<String> owned = new HashSet<>();
        String saved = meta.getPersistentDataContainer().get(key("generated_lore"), PersistentDataType.STRING);
        if (saved != null) for (String line : saved.split("\n", -1)) owned.add(ChatColor.stripColor(line));
        for (NamespacedKey stored : meta.getPersistentDataContainer().getKeys()) {
            if (!stored.getNamespace().equals(plugin.getName().toLowerCase(Locale.ROOT))) continue;
            if (!meta.getPersistentDataContainer().has(stored, PersistentDataType.INTEGER)) continue;
            Integer level = meta.getPersistentDataContainer().get(stored, PersistentDataType.INTEGER);
            if (level == null || level <= 0) continue;
            String id = stored.getKey();
            owned.add(names.getOrDefault(id, id) + " " + roman(level));
        }
        lore.removeIf(line -> line.startsWith("§0§0§r") || owned.contains(ChatColor.stripColor(line)));
    }
    public static String roman(int n) { return n <= 10 ? new String[]{"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"}[Math.max(0,n)] : String.valueOf(n); }
}
