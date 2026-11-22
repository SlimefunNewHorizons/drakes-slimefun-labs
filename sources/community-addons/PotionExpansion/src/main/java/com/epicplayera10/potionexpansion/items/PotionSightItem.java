package com.epicplayera10.potionexpansion.items;

import com.epicplayera10.potionexpansion.Settings;
import com.epicplayera10.potionexpansion.api.effects.PotionSightEffect;
import com.epicplayera10.potionexpansion.api.effects.PotionSightType;
import com.epicplayera10.potionexpansion.api.effects.EffectsManager;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemConsumptionHandler;

import org.bukkit.inventory.ItemStack;

public class PotionSightItem extends SlimefunItem {
    private final PotionSightType potionSightType;

    public PotionSightItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, PotionSightType potionSightType) {
        super(itemGroup, item, recipeType, recipe);

        this.potionSightType = potionSightType;
    }

    @Override
    public void preRegister() {
        addItemHandler(onConsume());
    }

    private ItemConsumptionHandler onConsume() {
        return (e, p, item) -> {
            EffectsManager.addEffect(p, new PotionSightEffect(potionSightType, Settings.getPotionDuration()));
        };
    }
}
