package com.agent1k.deathenchants.action;

import com.agent1k.deathenchants.utils.HexUtils;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Map;

public final class Actions {
    private final FileConfiguration config;
    public Actions(FileConfiguration config) { this.config = config; }
    public static String color(String value) { return HexUtils.color(value); }
    public void run(CommandSender sender, String key, Map<String, String> variables) {
        for (String line : config.getStringList(key)) {
            for (var entry : variables.entrySet()) line = line.replace("{" + entry.getKey() + "}", entry.getValue());
            int end = line.indexOf(']');
            if (end < 0) continue;
            String type = line.substring(0, end + 1);
            String body = color(line.substring(end + 1).trim());
            if (type.equals("[message]")) sender.sendMessage(body);
            if (!(sender instanceof Player player)) continue;
            if (type.equals("[title]")) {
                String[] parts = body.split("\\|", 2);
                player.sendTitle(parts[0], parts.length > 1 ? parts[1] : "", 10, 60, 10);
            }
            if (type.equals("[actionbar]")) player.sendActionBar(body);
            if (type.equals("[sound]")) {
                String[] parts = body.split(" ");
                try { player.playSound(player.getLocation(), Sound.valueOf(parts[0]), parts.length > 1 ? Float.parseFloat(parts[1]) : 1, parts.length > 2 ? Float.parseFloat(parts[2]) : 1); } catch (IllegalArgumentException ignored) { }
            }
        }
    }
}
