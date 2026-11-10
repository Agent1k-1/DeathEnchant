package com.agent1k.command.tab;

import com.agent1k.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class EnchantTabCompleter implements TabCompleter {
    private final List<SubCommand> subcommands;

    public EnchantTabCompleter(List<SubCommand> subcommands) {
        this.subcommands = List.copyOf(subcommands);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) return List.of();
        List<String> choices;
        if (args.length == 1) {
            choices = subcommands.stream().filter(sub -> sub.canUse(sender)).map(SubCommand::name).toList();
        } else {
            SubCommand subcommand = subcommands.stream()
                    .filter(sub -> sub.name().equalsIgnoreCase(args[0]) && sub.canUse(sender))
                    .findFirst().orElse(null);
            if (subcommand == null) return List.of();
            choices = subcommand.complete(sender, Arrays.copyOfRange(args, 1, args.length));
        }
        String prefix = args[args.length - 1].toLowerCase(Locale.ROOT);
        return choices.stream().filter(value -> value.toLowerCase(Locale.ROOT).startsWith(prefix)).toList();
    }
}
