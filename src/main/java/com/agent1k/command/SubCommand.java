package com.agent1k.command;

import org.bukkit.command.CommandSender;
import java.util.List;

public interface SubCommand {
    String name();

    default String permission() {
        return "";
    }

    default boolean canUse(CommandSender sender) {
        return permission().isEmpty() || sender.hasPermission(permission());
    }

    void execute(CommandSender sender, String[] args);

    default List<String> complete(CommandSender sender, String[] args) {
        return List.of();
    }
}
