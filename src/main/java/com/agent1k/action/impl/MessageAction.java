package com.agent1k.action.impl;

import com.agent1k.action.Actions;
import org.bukkit.command.CommandSender;

public final class MessageAction implements Actions {
    @Override
    public void execute(CommandSender sender, String body) {
        sender.sendMessage(body);
    }
}
