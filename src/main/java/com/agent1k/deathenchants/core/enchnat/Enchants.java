package com.agent1k.deathenchants.core.enchnat;

import com.agent1k.deathenchants.core.inst.Target;
import com.agent1k.deathenchants.service.EnchantItemService;

import com.agent1k.deathenchants.DeathEnchants;
import org.bukkit.*;
import org.bukkit.inventory.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class Enchants {
    public record Definition(String id, String name, int max, int weight, double chance, Target target, List<Double> values) { }
    private final Map<String, Definition> definitions;
    private final EnchantItemService items;

    public Enchants(DeathEnchants plugin, Map<String, Definition> definitions, Map<String, String> names) {
        this.definitions = Collections.unmodifiableMap(new LinkedHashMap<>(definitions));
        this.items = new EnchantItemService(plugin, this.definitions, names);
    }
    public Collection<Definition> all() { return definitions.values(); }
    public Definition get(String id) { return definitions.get(id); }
    public EnchantItemService items() { return items; }
    public int level(ItemStack item, String id) { return items.level(item, id); }
    public double value(ItemStack item, String id) { return items.value(item, id); }
    public ItemStack book(String id, int level) { return items.book(id, level); }
    public void apply(ItemStack item, String id, int level) { items.apply(item, id, level); }
    public void render(ItemStack item) { items.render(item); }
    public ItemStack clear(ItemStack source, ItemStack... inputs) { return items.clear(source, inputs); }
    public boolean hasCustom(ItemStack item) { return items.hasCustom(item); }
    public boolean roll(double chance) { return ThreadLocalRandom.current().nextDouble(100) < chance; }
    public Definition random(Material type) {
        var choices = all().stream().filter(d -> d.weight() > 0 && d.chance() > 0 && (type == Material.BOOK || type == Material.ENCHANTED_BOOK || d.target().accepts(type))).toList();
        int total = choices.stream().mapToInt(Definition::weight).sum();
        if (total == 0) return null;
        int n = ThreadLocalRandom.current().nextInt(total);
        for (var d : choices) { n -= d.weight(); if (n < 0) return roll(d.chance()) ? d : null; }
        return null;
    }
    public static String roman(int n) { return n <= 10 ? new String[]{"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"}[Math.max(0,n)] : String.valueOf(n); }
}
