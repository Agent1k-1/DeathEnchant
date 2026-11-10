package com.agent1k.core.looader;

import com.agent1k.DeathEnchants;
import com.agent1k.core.enchnat.Enchants;
import com.agent1k.core.inst.Target;
import com.agent1k.utils.HexUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class EnchantLoader {
    private final DeathEnchants plugin;

    public EnchantLoader(DeathEnchants plugin) {
        this.plugin = plugin;
    }

    public Enchants load() {
        File file = new File(plugin.getDataFolder(), "enchant.yml");
        if (!file.exists()) plugin.saveResource("enchant.yml", false);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        Map<String, Enchants.Definition> definitions = new LinkedHashMap<>();
        Map<String, String> names = new LinkedHashMap<>();

        for (String id : config.getKeys(false)) {
            String name = ChatColor.stripColor(HexUtils.color(config.getString(id + ".name", id)));
            names.put(id, name);
            if (!config.getBoolean(id + ".enabled", true)) continue;

            try {
                Target target = id.equals("pinger")
                        ? Target.TOOLS
                        : Target.valueOf(config.getString(id + ".target", "ALL"));
                Enchants.Definition definition = new Enchants.Definition(
                        id,
                        ChatColor.GRAY + name,
                        Math.max(1, config.getInt(id + ".max-level", 1)),
                        Math.max(0, config.getInt(id + ".weight", 10)),
                        Math.max(0, Math.min(100, config.getDouble(id + ".chance", 100))),
                        target,
                        List.copyOf(config.getDoubleList(id + ".values"))
                );
                definitions.put(id, definition);
            } catch (IllegalArgumentException error) {
                plugin.getLogger().warning("Invalid enchant: " + id);
            }
        }

        return new Enchants(plugin, definitions, names);
    }
}
