package com.epicplayera10.potionexpansion.multiblocks.alchemic;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class AlchemicRecipe {

    private final ItemStack ingredient;
    private final PotionType inputPotion;
    private final ItemStack output;

    @ParametersAreNonnullByDefault
    public AlchemicRecipe(List<ItemStack> input, ItemStack output) {
        this.ingredient = extractIngredient(input);
        this.inputPotion = extractInputPotion(input);
        this.output = output;
    }

    @Nullable
    public static AlchemicRecipe from(@Nullable List<ItemStack> input, @Nullable ItemStack output) {
        if (input == null || input.isEmpty() || output == null) {
            return null;
        }
        ItemStack ingredient = extractIngredient(input);
        if (ingredient == null) {
            return null;
        }
        return new AlchemicRecipe(input, output);
    }

    @Nullable
    private static ItemStack extractIngredient(@Nonnull List<ItemStack> input) {
        if (input.size() > 1 && input.get(1) != null) {
            return input.get(1);
        }
        if (input.size() > 4 && input.get(4) != null) {
            return input.get(4);
        }
        return null;
    }

    @Nullable
    private static PotionType extractInputPotion(@Nonnull List<ItemStack> input) {
        int[] candidateSlots = {7, 6, 8};
        for (int slot : candidateSlots) {
            if (slot < input.size() && input.get(slot) != null && input.get(slot).getType() == Material.POTION) {
                return getPotionType(input.get(slot));
            }
        }
        return null;
    }

    @Nullable
    static PotionType getPotionType(@Nullable ItemStack potion) {
        if (potion != null && potion.getType() == Material.POTION && potion.hasItemMeta()
                && potion.getItemMeta() instanceof PotionMeta meta) {
            return meta.getBasePotionType();
        }
        return null;
    }

    public ItemStack getIngredient() {
        return ingredient;
    }

    @Nullable
    public PotionType getInputPotion() {
        return inputPotion;
    }

    public ItemStack getOutput() {
        return output;
    }
}
