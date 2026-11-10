package com.agent1k.command.sub;

import com.agent1k.DeathEnchants;
import com.agent1k.command.SubCommand;
import org.bukkit.command.CommandSender;
import java.util.Map;

public final class ReloadCMD implements SubCommand {
    private final DeathEnchants plugin;

    public ReloadCMD(DeathEnchants plugin) {
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public String permission() {
        return "deathenchants.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.load();
        plugin.actions.run(sender, "reloaded", Map.of());
    }
}
