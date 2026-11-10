package com.agent1k.deathenchants.action.impl;

import com.agent1k.deathenchants.action.Actions;
import org.bukkit.command.CommandSender;

public final class MessageAction implements Actions {
    @Override
    public void execute(CommandSender sender, String body) {
        sender.sendMessage(body);
    }
}
