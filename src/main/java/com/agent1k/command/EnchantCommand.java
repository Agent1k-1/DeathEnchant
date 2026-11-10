package com.agent1k.command;

import com.agent1k.DeathEnchants;
import com.agent1k.command.sub.GiveCMD;
import com.agent1k.command.sub.ListCMD;
import com.agent1k.command.sub.ReloadCMD;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class EnchantCommand implements CommandExecutor {
    private final DeathEnchants plugin;
    private final List<SubCommand> subcommands;

    public EnchantCommand(DeathEnchants plugin) {
        this.plugin = plugin;
        this.subcommands = List.of(new GiveCMD(plugin), new ReloadCMD(plugin), new ListCMD(plugin));
    }

    public List<SubCommand> subcommands() {
        return subcommands;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            plugin.actions.run(sender, "help", Map.of());
            return true;
        }
        String name = args[0].toLowerCase(Locale.ROOT);
        SubCommand subcommand = subcommands.stream().filter(sub -> sub.name().equals(name)).findFirst().orElse(null);
        if (subcommand == null) {
            plugin.actions.run(sender, "error", Map.of());
        } else if (!subcommand.canUse(sender)) {
            plugin.actions.run(sender, "permission", Map.of());
        } else {
            subcommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
        }
        return true;
    }
}
