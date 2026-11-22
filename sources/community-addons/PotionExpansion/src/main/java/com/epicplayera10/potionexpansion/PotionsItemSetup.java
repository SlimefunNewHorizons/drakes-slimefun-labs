package com.epicplayera10.potionexpansion;

import com.epicplayera10.potionexpansion.items.PotionItems;
import com.epicplayera10.potionexpansion.items.PotionRecipeType;
import com.epicplayera10.potionexpansion.api.effects.PotionSightType;
import com.epicplayera10.potionexpansion.items.PotionSightItem;
import com.epicplayera10.potionexpansion.multiblocks.alchemic.AlchemicStation;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.UnplaceableBlock;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class PotionsItemSetup {
    public static void setup(@Nonnull PotionExpansion plugin) {
        new AlchemicStation(PotionItems.potionCategory, PotionItems.ALCHEMIC_STATION
        ).register(plugin);

        // Pestle
        new SlimefunItem(PotionItems.potionCategory, PotionItems.PESTLE, RecipeType.ENHANCED_CRAFTING_TABLE,
                new ItemStack[]{
                        null, new ItemStack(Material.QUARTZ),  null,
                        null, new ItemStack(Material.QUARTZ),  null,
                        null, new ItemStack(Material.DIAMOND), null
                }
        ).register(plugin);

        powdersSetup(plugin);
        potionsSetup(plugin);

        plugin.getLogger().info("Loaded items!");
    }

    private static void potionsSetup(@Nonnull PotionExpansion plugin) {
        new PotionSightItem(PotionItems.potionCategory, PotionItems.COAL_SIGHT, PotionRecipeType.ALCHEMIC_STATION_RECIPE,
                new ItemStack[]{null, PotionItems.COAL_POWDER, null, null, null, null, PotionItems.mundanePotion, PotionItems.mundanePotion, PotionItems.mundanePotion},
                PotionSightType.COAL_SIGHT
        ).register(plugin);

        new PotionSightItem(PotionItems.potionCategory, PotionItems.IRON_SIGHT, PotionRecipeType.ALCHEMIC_STATION_RECIPE,
                new ItemStack[]{null, PotionItems.IRON_POWDER, null, null, null, null, PotionItems.mundanePotion, PotionItems.mundanePotion, PotionItems.mundanePotion},
                PotionSightType.IRON_SIGHT
        ).register(plugin);

        new PotionSightItem(PotionItems.potionCategory, PotionItems.DIAMOND_SIGHT, PotionRecipeType.ALCHEMIC_STATION_RECIPE,
                new ItemStack[]{null, PotionItems.DIAMOND_POWDER, null, null, null, null, PotionItems.mundanePotion, PotionItems.mundanePotion, PotionItems.mundanePotion},
                PotionSightType.DIAMOND_SIGHT
        ).register(plugin);

        new PotionSightItem(PotionItems.potionCategory, PotionItems.GOLD_SIGHT, PotionRecipeType.ALCHEMIC_STATION_RECIPE,
                new ItemStack[]{null, PotionItems.GOLD_POWDER, null, null, null, null, PotionItems.mundanePotion, PotionItems.mundanePotion, PotionItems.mundanePotion},
                PotionSightType.GOLD_SIGHT
        ).register(plugin);

        new PotionSightItem(PotionItems.potionCategory, PotionItems.LAPIS_SIGHT, PotionRecipeType.ALCHEMIC_STATION_RECIPE,
                new ItemStack[]{null, PotionItems.LAPIS_POWDER, null, null, null, null, PotionItems.mundanePotion, PotionItems.mundanePotion, PotionItems.mundanePotion},
                PotionSightType.LAPIS_SIGHT
        ).register(plugin);

        new PotionSightItem(PotionItems.potionCategory, PotionItems.REDSTONE_SIGHT, PotionRecipeType.ALCHEMIC_STATION_RECIPE,
                new ItemStack[]{null, PotionItems.REDSTONE_POWDER, null, null, null, null, PotionItems.mundanePotion, PotionItems.mundanePotion, PotionItems.mundanePotion},
                PotionSightType.REDSTONE_SIGHT
        ).register(plugin);

        new PotionSightItem(PotionItems.potionCategory, PotionItems.EMERALD_SIGHT, PotionRecipeType.ALCHEMIC_STATION_RECIPE,
                new ItemStack[]{null, PotionItems.EMERALD_POWDER, null, null, null, null, PotionItems.mundanePotion, PotionItems.mundanePotion, PotionItems.mundanePotion},
                PotionSightType.EMERALD_SIGHT
        ).register(plugin);

        new PotionSightItem(PotionItems.potionCategory, PotionItems.QUARTZ_SIGHT, PotionRecipeType.ALCHEMIC_STATION_RECIPE,
                new ItemStack[]{null, PotionItems.QUARTZ_POWDER, null, null, null, null, PotionItems.mundanePotion, PotionItems.mundanePotion, PotionItems.mundanePotion},
                PotionSightType.QUARTZ_SIGHT
        ).register(plugin);

        MinecraftVersion minecraftVersion = Slimefun.getMinecraftVersion();

        if (minecraftVersion.isAtLeast(MinecraftVersion.MINECRAFT_1_16)) {
            new PotionSightItem(PotionItems.potionCategory, PotionItems.ANCIENT_DEBRIS_SIGHT, PotionRecipeType.ALCHEMIC_STATION_RECIPE,
                    new ItemStack[]{null, PotionItems.ANCIENT_DEBRIS_POWDER, null, null, null, null, PotionItems.mundanePotion, PotionItems.mundanePotion, PotionItems.mundanePotion},
                    PotionSightType.ANCIENT_DEBRIS_SIGHT
            ).register(plugin);
        }

        if (minecraftVersion.isAtLeast(MinecraftVersion.MINECRAFT_1_17)) {
            new PotionSightItem(PotionItems.potionCategory, PotionItems.COPPER_SIGHT, PotionRecipeType.ALCHEMIC_STATION_RECIPE,
                    new ItemStack[]{null, PotionItems.COPPER_POWDER, null, null, null, null, PotionItems.mundanePotion, PotionItems.mundanePotion, PotionItems.mundanePotion},
                    PotionSightType.COPPER_SIGHT
            ).register(plugin);
        }
    }

    private static void powdersSetup(@Nonnull PotionExpansion plugin) {
        new SlimefunItem(PotionItems.potionCategory, PotionItems.COAL_POWDER, RecipeType.ENHANCED_CRAFTING_TABLE,
                new ItemStack[]{PotionItems.PESTLE, new ItemStack(Material.COAL_ORE), null, null, null, null, null, null, null}
        ).register(plugin);

        new SlimefunItem(PotionItems.potionCategory, PotionItems.IRON_POWDER, RecipeType.ENHANCED_CRAFTING_TABLE,
                new ItemStack[]{PotionItems.PESTLE, new ItemStack(Material.IRON_ORE), null, null, null, null, null, null, null}
        ).register(plugin);

        new SlimefunItem(PotionItems.potionCategory, PotionItems.DIAMOND_POWDER, RecipeType.ENHANCED_CRAFTING_TABLE,
                new ItemStack[]{PotionItems.PESTLE, new ItemStack(Material.DIAMOND_ORE), null, null, null, null, null, null, null}
        ).register(plugin);

        new SlimefunItem(PotionItems.potionCategory, PotionItems.GOLD_POWDER, RecipeType.ENHANCED_CRAFTING_TABLE,
                new ItemStack[]{PotionItems.PESTLE, new ItemStack(Material.GOLD_ORE), null, null, null, null, null, null, null}
        ).register(plugin);

        new SlimefunItem(PotionItems.potionCategory, PotionItems.LAPIS_POWDER, RecipeType.ENHANCED_CRAFTING_TABLE,
                new ItemStack[]{PotionItems.PESTLE, new ItemStack(Material.LAPIS_ORE), null, null, null, null, null, null, null}
        ).register(plugin);

        new UnplaceableBlock(PotionItems.potionCategory, PotionItems.REDSTONE_POWDER, RecipeType.ENHANCED_CRAFTING_TABLE,
                new ItemStack[]{PotionItems.PESTLE, new ItemStack(Material.REDSTONE_ORE), null, null, null, null, null, null, null}
        ).register(plugin);

        new SlimefunItem(PotionItems.potionCategory, PotionItems.EMERALD_POWDER, RecipeType.ENHANCED_CRAFTING_TABLE,
                new ItemStack[]{PotionItems.PESTLE, new ItemStack(Material.EMERALD_ORE), null, null, null, null, null, null, null}
        ).register(plugin);

        new SlimefunItem(PotionItems.potionCategory, PotionItems.QUARTZ_POWDER, RecipeType.ENHANCED_CRAFTING_TABLE,
                new ItemStack[]{PotionItems.PESTLE, new ItemStack(Material.NETHER_QUARTZ_ORE), null, null, null, null, null, null, null}
        ).register(plugin);

        MinecraftVersion minecraftVersion = Slimefun.getMinecraftVersion();

        if (minecraftVersion.isAtLeast(MinecraftVersion.MINECRAFT_1_16)) {
            new UnplaceableBlock(PotionItems.potionCategory, PotionItems.ANCIENT_DEBRIS_POWDER, RecipeType.ENHANCED_CRAFTING_TABLE,
                    new ItemStack[]{PotionItems.PESTLE, new ItemStack(Material.ANCIENT_DEBRIS), null, null, null, null, null, null, null}
            ).register(plugin);
        }

        if (minecraftVersion.isAtLeast(MinecraftVersion.MINECRAFT_1_17)) {
            new SlimefunItem(PotionItems.potionCategory, PotionItems.COPPER_POWDER, RecipeType.ENHANCED_CRAFTING_TABLE,
                    new ItemStack[]{PotionItems.PESTLE, new ItemStack(Material.COPPER_ORE), null, null, null, null, null, null, null}
            ).register(plugin);
        }
    }
}
