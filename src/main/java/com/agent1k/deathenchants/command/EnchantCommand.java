package com.agent1k.deathenchants.command;

import com.agent1k.deathenchants.DeathEnchants;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import java.util.*;

public final class EnchantCommand implements CommandExecutor, TabCompleter {
    private final DeathEnchants plugin;
    public EnchantCommand(DeathEnchants plugin) { this.plugin = plugin; }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) { plugin.actions.run(sender, "help", Map.of()); return true; }
        if (args[0].equalsIgnoreCase("list")) { for (var d : plugin.enchants.all()) sender.sendMessage(d.id() + " — " + d.name() + " (1–" + d.max() + ")"); return true; }
        if (!sender.hasPermission("deathenchants.admin")) { plugin.actions.run(sender, "permission", Map.of()); return true; }
        if (args[0].equalsIgnoreCase("reload")) { plugin.load(); plugin.actions.run(sender, "reloaded", Map.of()); return true; }
        try {
            if (!args[0].equalsIgnoreCase("give") || args.length < 3) throw new IllegalArgumentException();
            var player = Bukkit.getPlayerExact(args[1]);
            var d = plugin.enchants.get(args[2].toLowerCase(Locale.ROOT));
            int level = args.length > 3 ? Integer.parseInt(args[3]) : 1;
            if (player == null || d == null || level < 1 || level > d.max()) throw new IllegalArgumentException();
            player.getInventory().addItem(plugin.enchants.book(d.id(), level)).values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
            plugin.actions.run(sender, "given", Map.of("player", player.getName()));
        } catch (IllegalArgumentException error) { plugin.actions.run(sender, "error", Map.of()); }
        return true;
    }
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) return List.of();
        boolean admin = sender.hasPermission("deathenchants.admin");
        List<String> choices;
        if (args.length == 1) {
            choices = admin ? List.of("give", "reload", "list") : List.of("list");
        } else if (!admin || !args[0].equalsIgnoreCase("give")) {
            return List.of();
        } else if (args.length == 2) {
            choices = Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).toList();
        } else if (args.length == 3) {
            choices = plugin.enchants.all().stream().map(d -> d.id()).toList();
        } else if (args.length == 4) {
            var enchantment = plugin.enchants.get(args[2].toLowerCase(Locale.ROOT));
            if (enchantment == null) return List.of();
            choices = java.util.stream.IntStream.rangeClosed(1, enchantment.max())
                    .mapToObj(Integer::toString).toList();
        } else {
            return List.of();
        }
        String prefix = args[args.length - 1].toLowerCase(Locale.ROOT);
        return choices.stream().filter(value -> value.toLowerCase(Locale.ROOT).startsWith(prefix)).toList();
    }
}
