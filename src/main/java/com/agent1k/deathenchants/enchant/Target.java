package com.agent1k.deathenchants.enchant;

import org.bukkit.Material;
public enum Target {
    ALL, TOOLS, TOOLS_NO_HOE, WEAPONS, BOOTS, HOES, AXES;
    public boolean accepts(Material material) {
        String n = material.name();
        boolean tool = n.endsWith("_PICKAXE") || n.endsWith("_AXE") || n.endsWith("_SHOVEL") || n.endsWith("_HOE") || material == Material.SHEARS;
        boolean weapon = n.endsWith("_SWORD") || n.endsWith("_AXE") || n.equals("BOW") || n.equals("CROSSBOW") || n.equals("TRIDENT");
        return switch (this) {
            case ALL -> tool || weapon || n.endsWith("_HELMET") || n.endsWith("_CHESTPLATE") || n.endsWith("_LEGGINGS") || n.endsWith("_BOOTS") || n.equals("ELYTRA") || n.equals("SHIELD") || n.equals("FISHING_ROD") || n.equals("FLINT_AND_STEEL");
            case TOOLS -> tool;
            case TOOLS_NO_HOE -> tool && !n.endsWith("_HOE");
            case WEAPONS -> weapon;
            case BOOTS -> n.endsWith("_BOOTS");
            case HOES -> n.endsWith("_HOE");
            case AXES -> n.endsWith("_AXE");
        };
    }
}
