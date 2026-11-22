package com.epicplayera10.potionexpansion.items;

import com.epicplayera10.potionexpansion.PotionExpansion;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.NamespacedKey;

public class PotionRecipeType {
    public static final RecipeType ALCHEMIC_STATION_RECIPE = new RecipeType(
        new NamespacedKey(PotionExpansion.getInstance(), "alchemic_station"),
        PotionItems.ALCHEMIC_STATION
    );
}
