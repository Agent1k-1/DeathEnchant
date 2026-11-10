package com.agent1k.deathenchants.core.enchnat;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.FurnaceRecipe;

public final class SmeltingEnchant {
    public ItemStack smelt(ItemStack item) {
        var recipes = Bukkit.recipeIterator();
        while (recipes.hasNext()) {
            Recipe recipe = recipes.next();
            if (recipe instanceof FurnaceRecipe furnace && furnace.getInputChoice().test(item)) { ItemStack result = furnace.getResult().clone(); result.setAmount(result.getAmount() * item.getAmount()); item = result; break; }
        }
        return item;
    }
}
