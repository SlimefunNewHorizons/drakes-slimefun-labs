package com.epicplayera10.potionexpansion.multiblocks.alchemic;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

class AlchemicStationTest {

    private static ItemStack mockPotion(PotionType baseType, boolean hasMeta, boolean hasCustomEffects) {
        ItemStack stack = mock(ItemStack.class);
        when(stack.getType()).thenReturn(Material.POTION);
        when(stack.hasItemMeta()).thenReturn(hasMeta);
        if (hasMeta) {
            PotionMeta meta = mock(PotionMeta.class);
            when(meta.getBasePotionType()).thenReturn(baseType);
            when(meta.hasCustomEffects()).thenReturn(hasCustomEffects);
            when(stack.getItemMeta()).thenReturn(meta);
        }
        return stack;
    }

    private static ItemStack mockPowder() {
        ItemStack powder = mock(ItemStack.class);
        when(powder.getType()).thenReturn(Material.GUNPOWDER);
        return powder;
    }

    private static ItemStack mockOutput() {
        ItemStack out = mock(ItemStack.class);
        when(out.clone()).thenReturn(out);
        return out;
    }

    private static AlchemicRecipe createMundaneRecipe(ItemStack powder, ItemStack output) {
        ItemStack mundane = mockPotion(PotionType.MUNDANE, true, false);
        List<ItemStack> grid = Arrays.asList(
                null, powder, null,
                null, null, null,
                mundane, mundane, mundane
        );
        return AlchemicRecipe.from(grid, output);
    }

    @Test
    void recipeExtractsIngredientAndMundaneBase() {
        ItemStack powder = mockPowder();
        ItemStack output = mockOutput();
        AlchemicRecipe recipe = createMundaneRecipe(powder, output);

        assertNotNull(recipe);
        assertSame(powder, recipe.getIngredient());
        assertEquals(PotionType.MUNDANE, recipe.getInputPotion());
        assertSame(output, recipe.getOutput());
    }

    @Test
    void acceptsMundaneWaterAndPlainBlankPotion() {
        AlchemicRecipe recipe = createMundaneRecipe(mockPowder(), mockOutput());

        try (MockedStatic<SlimefunItem> sfItem = mockStatic(SlimefunItem.class)) {
            sfItem.when(() -> SlimefunItem.getByItem(any())).thenReturn(null);

            assertTrue(AlchemicStation.isInputPotionValid(recipe, mockPotion(PotionType.MUNDANE, true, false)));
            assertTrue(AlchemicStation.isInputPotionValid(recipe, mockPotion(PotionType.WATER, true, false)));
            assertTrue(AlchemicStation.isInputPotionValid(recipe, mockPotion(null, false, false)));
            assertTrue(AlchemicStation.isInputPotionValid(recipe, mockPotion(null, true, false)));
        }
    }

    @Test
    void rejectsEffectPotionsCustomEffectsAndSlimefunPotions() {
        AlchemicRecipe recipe = createMundaneRecipe(mockPowder(), mockOutput());

        try (MockedStatic<SlimefunItem> sfItem = mockStatic(SlimefunItem.class)) {
            sfItem.when(() -> SlimefunItem.getByItem(any())).thenReturn(null);

            assertFalse(AlchemicStation.isInputPotionValid(recipe, mockPotion(PotionType.HEALING, true, false)));
            assertFalse(AlchemicStation.isInputPotionValid(recipe, mockPotion(PotionType.AWKWARD, true, false)));
            assertFalse(AlchemicStation.isInputPotionValid(recipe, mockPotion(PotionType.WATER, true, true)));

            // Rejects already-crafted Slimefun sight potions even if basePotionType is null
            ItemStack sightPotion = mockPotion(null, true, false);
            SlimefunItem fakeSf = mock(SlimefunItem.class);
            sfItem.when(() -> SlimefunItem.getByItem(sightPotion)).thenReturn(fakeSf);
            assertFalse(AlchemicStation.isInputPotionValid(recipe, sightPotion));
        }
    }

    @Test
    void matchesPowderInTopCenterOrCenterSlotWithWaterOrMundane() {
        ItemStack powder = mockPowder();
        ItemStack output = mockOutput();
        AlchemicRecipe recipe = createMundaneRecipe(powder, output);
        ItemStack waterBottle = mockPotion(PotionType.WATER, true, false);
        ItemStack mundaneBottle = mockPotion(PotionType.MUNDANE, true, false);

        try (MockedStatic<SlimefunItem> sfItem = mockStatic(SlimefunItem.class);
             MockedStatic<SlimefunUtils> sfUtils = mockStatic(SlimefunUtils.class)) {
            sfItem.when(() -> SlimefunItem.getByItem(any())).thenReturn(null);
            sfUtils.when(() -> SlimefunUtils.isItemSimilar(any(), any(), anyBoolean()))
                    .thenAnswer(inv -> inv.getArgument(0) == inv.getArgument(1));

            // Powder in slot 1 (top-center) + water in slot 6
            Inventory invTop = mock(Inventory.class);
            when(invTop.getItem(AlchemicStation.INGREDIENT_SLOT)).thenReturn(powder);
            when(invTop.getItem(6)).thenReturn(waterBottle);
            AlchemicStation.MatchedRecipe matchedTop = AlchemicStation.matchRecipe(Collections.singletonList(recipe), invTop);
            assertNotNull(matchedTop);
            assertEquals(AlchemicStation.INGREDIENT_SLOT, matchedTop.ingredientSlot());

            // Powder in slot 4 (center) + mundane in slot 7
            Inventory invCenter = mock(Inventory.class);
            when(invCenter.getItem(AlchemicStation.CENTER_INGREDIENT_SLOT)).thenReturn(powder);
            when(invCenter.getItem(7)).thenReturn(mundaneBottle);
            AlchemicStation.MatchedRecipe matchedCenter = AlchemicStation.matchRecipe(Collections.singletonList(recipe), invCenter);
            assertNotNull(matchedCenter);
            assertEquals(AlchemicStation.CENTER_INGREDIENT_SLOT, matchedCenter.ingredientSlot());
        }
    }

    @Test
    void brewConvertsUpToThreeValidPotions() {
        ItemStack output = mockOutput();
        AlchemicRecipe recipe = createMundaneRecipe(mockPowder(), output);
        ItemStack p6 = mockPotion(PotionType.WATER, true, false);
        ItemStack p7 = mockPotion(PotionType.MUNDANE, true, false);
        ItemStack p8 = mockPotion(PotionType.WATER, true, false);
        ItemStack p3 = mockPotion(PotionType.WATER, true, false);
        Inventory inv = mock(Inventory.class);
        when(inv.getItem(6)).thenReturn(p6);
        when(inv.getItem(7)).thenReturn(p7);
        when(inv.getItem(8)).thenReturn(p8);
        when(inv.getItem(3)).thenReturn(p3);

        try (MockedStatic<SlimefunItem> sfItem = mockStatic(SlimefunItem.class)) {
            sfItem.when(() -> SlimefunItem.getByItem(any())).thenReturn(null);

            AlchemicStation.brew(recipe, inv);

            verify(inv).setItem(6, output);
            verify(inv).setItem(7, output);
            verify(inv).setItem(8, output);
            verify(inv, never()).setItem(eq(3), any());
        }
    }
}
