package com.agent1k.action.impl;

import com.agent1k.action.Actions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class TitleAction implements Actions {
    @Override
    public void execute(CommandSender sender, String body) {
        if (!(sender instanceof Player player)) return;
        String[] parts = body.split("\\|", 2);
        player.sendTitle(parts[0], parts.length > 1 ? parts[1] : "", 10, 60, 10);
    }
}
