package com.agent1k.deathenchants;

import com.agent1k.deathenchants.core.enchnat.Enchants;
import com.agent1k.deathenchants.anvil.AnvilListener;
import com.agent1k.deathenchants.action.Actions;
import com.agent1k.deathenchants.command.EnchantCommand;
import com.agent1k.deathenchants.listener.Effects;
import com.agent1k.deathenchants.listener.Interfaces;
import com.agent1k.deathenchants.listener.ItemRefresh;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public final class DeathEnchants extends JavaPlugin {
    public Enchants enchants;
    public Actions actions;
    public void onEnable() {
        saveDefaultConfig();
        for (String file : new String[]{"enchant.yml", "messages.yml"}) if (!new File(getDataFolder(), file).exists()) saveResource(file, false);
        load();
        EnchantCommand command = new EnchantCommand(this);
        getCommand("deathenchants").setExecutor(command);
        getCommand("deathenchants").setTabCompleter(command);
        getServer().getPluginManager().registerEvents(new Effects(this), this);
        getServer().getPluginManager().registerEvents(new Interfaces(this), this);
        getServer().getPluginManager().registerEvents(new AnvilListener(this), this);
        ItemRefresh refresh = new ItemRefresh(this);
        getServer().getPluginManager().registerEvents(refresh, this);
        getServer().getOnlinePlayers().forEach(refresh::refresh);
    }
    public void load() {
        reloadConfig();
        enchants = new Enchants(this, YamlConfiguration.loadConfiguration(new File(getDataFolder(), "enchant.yml")));
        actions = new Actions(YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml")));
    }
}
