package com.agent1k.core.enchnat;

import com.agent1k.DeathEnchants;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayDeque;

public final class LumberjackEnchant {
    private final DeathEnchants plugin;
    public LumberjackEnchant(DeathEnchants plugin) { this.plugin = plugin; }
    public List<Block> blocks(Block origin) {
        List<Block> blocks = new ArrayList<>();
        Set<Block> visited = new HashSet<>(); ArrayDeque<Block> queue = new ArrayDeque<>(); queue.add(origin); visited.add(origin);
        int limit = Math.max(1, Math.min(1024, plugin.getConfig().getInt("limits.tree-blocks", 128)));
        while (!queue.isEmpty() && visited.size() < limit) {
            Block b = queue.remove();
            for (int x = -1; x <= 1; x++) for (int y = 0; y <= 1; y++) for (int z = -1; z <= 1; z++) {
                Block next = b.getRelative(x,y,z);
                if (Tag.LOGS.isTagged(next.getType()) && visited.size() < limit && visited.add(next)) { queue.add(next); blocks.add(next); }
            }
        }
        return blocks;
    }
}
