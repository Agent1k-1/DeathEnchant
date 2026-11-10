package com.agent1k.core.enchnat;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.ArrayList;

public final class DrillEnchant {
    public List<Block> blocks(Player p, Block origin) {
        List<Block> blocks = new ArrayList<>();
        var direction = p.getLocation().getDirection();
        for (int a = -1; a <= 1; a++) for (int b = -1; b <= 1; b++) {
            if (a == 0 && b == 0) continue;
            Block next = Math.abs(direction.getY()) > 0.6 ? origin.getRelative(a,0,b) : Math.abs(direction.getX()) > Math.abs(direction.getZ()) ? origin.getRelative(0,a,b) : origin.getRelative(a,b,0);
            if (!next.getType().isAir() && next.getType().getHardness() >= 0 && !(next.getState() instanceof org.bukkit.inventory.InventoryHolder)) blocks.add(next);
        }
        return blocks;
    }
}
