package com.agent1k.action.impl;

import com.agent1k.action.Actions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ActionBarAction implements Actions {
    @Override
    public void execute(CommandSender sender, String body) {
        if (sender instanceof Player player) player.sendActionBar(body);
    }
}
