package com.agent1k.deathenchants.action;

import com.agent1k.deathenchants.utils.HexUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.Map;

public final class ActionExecutor {
    private final FileConfiguration config;

    public ActionExecutor(FileConfiguration config) {
        this.config = config;
    }

    public void run(CommandSender sender, String key, Map<String, String> variables) {
        for (String configured : config.getStringList(key)) {
            String line = configured.trim();
            int end = line.indexOf(']');
            if (end < 0) continue;
            var type = ActionType.fromTag(line.substring(0, end + 1));
            if (type.isEmpty()) continue;
            String body = line.substring(end + 1).trim();
            for (var entry : variables.entrySet()) {
                body = body.replace("{" + entry.getKey() + "}", entry.getValue());
            }
            type.get().action().execute(sender, HexUtils.color(body));
        }
    }
}
