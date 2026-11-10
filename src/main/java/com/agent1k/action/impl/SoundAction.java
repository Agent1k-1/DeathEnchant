package com.agent1k.action.impl;

import com.agent1k.action.Actions;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class SoundAction implements Actions {
    @Override
    public void execute(CommandSender sender, String body) {
        if (!(sender instanceof Player player) || body.isBlank()) return;
        String[] parts = body.trim().split("\\s+");
        try {
            Sound sound = Sound.valueOf(parts[0]);
            float volume = parts.length > 1 ? Float.parseFloat(parts[1]) : 1;
            float pitch = parts.length > 2 ? Float.parseFloat(parts[2]) : 1;
            if (!Float.isFinite(volume) || !Float.isFinite(pitch) || volume < 0 || pitch < 0) return;
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException ignored) { }
    }
}
