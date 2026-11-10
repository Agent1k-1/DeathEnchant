package com.agent1k.deathenchants.command.sub;

import com.agent1k.deathenchants.DeathEnchants;
import com.agent1k.deathenchants.command.SubCommand;
import org.bukkit.command.CommandSender;

public final class ListCMD implements SubCommand {
    private final DeathEnchants plugin;

    public ListCMD(DeathEnchants plugin) {
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "list";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (var enchantment : plugin.enchants.all()) {
            sender.sendMessage(enchantment.id() + " — " + enchantment.name() + " (1–" + enchantment.max() + ")");
        }
    }
}
