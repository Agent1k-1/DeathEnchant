package com.agent1k.deathenchants.command.sub;

import com.agent1k.deathenchants.DeathEnchants;
import com.agent1k.deathenchants.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

public final class GiveCMD implements SubCommand {
    private final DeathEnchants plugin;

    public GiveCMD(DeathEnchants plugin) {
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "give";
    }

    @Override
    public String permission() {
        return "deathenchants.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            if (args.length < 2 || args.length > 3) throw new IllegalArgumentException();
            var player = Bukkit.getPlayerExact(args[0]);
            var enchantment = plugin.enchants.get(args[1].toLowerCase(Locale.ROOT));
            int level = args.length == 3 ? Integer.parseInt(args[2]) : 1;
            if (player == null || enchantment == null || level < 1 || level > enchantment.max()) {
                throw new IllegalArgumentException();
            }
            player.getInventory().addItem(plugin.enchants.book(enchantment.id(), level)).values()
                    .forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
            plugin.actions.run(sender, "given", Map.of("player", player.getName()));
        } catch (IllegalArgumentException error) {
            plugin.actions.run(sender, "error", Map.of());
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).toList();
        }
        if (args.length == 2) {
            return plugin.enchants.all().stream().map(enchantment -> enchantment.id()).toList();
        }
        if (args.length == 3) {
            var enchantment = plugin.enchants.get(args[1].toLowerCase(Locale.ROOT));
            if (enchantment != null) {
                return IntStream.rangeClosed(1, enchantment.max()).mapToObj(Integer::toString).toList();
            }
        }
        return List.of();
    }
}
