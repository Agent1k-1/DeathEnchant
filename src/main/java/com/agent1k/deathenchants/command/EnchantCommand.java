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
        List<String> choices = args.length == 1 ? List.of("give", "reload", "list") : args.length == 2 && args[0].equalsIgnoreCase("give") ? Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).toList() : args.length == 3 ? plugin.enchants.all().stream().map(d -> d.id()).toList() : List.of();
        return choices.stream().filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args[args.length - 1].toLowerCase(Locale.ROOT))).toList();
    }
}
