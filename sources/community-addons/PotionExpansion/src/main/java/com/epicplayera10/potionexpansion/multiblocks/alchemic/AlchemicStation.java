package com.epicplayera10.potionexpansion.multiblocks.alchemic;

import com.epicplayera10.potionexpansion.PotionExpansion;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;
import io.papermc.lib.PaperLib;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlchemicStation extends MultiBlockMachine {
    static final int INGREDIENT_SLOT = 1;
    static final int CENTER_INGREDIENT_SLOT = 4;
    private static final int[] INGREDIENT_SLOTS = {INGREDIENT_SLOT, CENTER_INGREDIENT_SLOT};
    private static final int[] POTION_SLOTS = {6, 7, 8, 3, 4, 5, 0, 1, 2};
    private static final int MAX_POTIONS_PER_BREW = 3;

    private final List<AlchemicRecipe> alchemicRecipes = new ArrayList<>();

    record MatchedRecipe(AlchemicRecipe recipe, int ingredientSlot) {}

    @ParametersAreNonnullByDefault
    public AlchemicStation(ItemGroup itemGroup, SlimefunItemStack item) {
        super(itemGroup, item, new ItemStack[]{null, new ItemStack(Material.WHITE_STAINED_GLASS), null,
                new ItemStack(Material.WHITE_STAINED_GLASS), new ItemStack(Material.GLOWSTONE), new ItemStack(Material.WHITE_STAINED_GLASS),
                new ItemStack(Material.CAULDRON), new ItemStack(Material.DISPENSER), new ItemStack(Material.CAULDRON)}, BlockFace.SELF);
    }

    @Override
    public void addRecipe(ItemStack[] input, ItemStack output) {
        super.addRecipe(input, output);
        if (input != null && output != null) {
            AlchemicRecipe parsed = AlchemicRecipe.from(Arrays.asList(input), output);
            if (parsed != null) {
                alchemicRecipes.add(parsed);
            }
        }
    }

    @Override
    public void onInteract(Player player, Block block) {
        Block dispBlock = block.getRelative(BlockFace.DOWN);
        BlockState state = PaperLib.getBlockState(dispBlock, false).getState();

        if (state instanceof Dispenser disp) {
            Inventory inv = disp.getInventory();

            List<Block> glassBlocks = getWhiteGlassBlocks(block);

            MatchedRecipe matched = checkRecipe(inv);

            if (matched != null) {
                AlchemicRecipe recipe = matched.recipe();
                // En Laboratorio (modo creativo/prueba) se permite preparar sin research previa:
                // los testers como StoneAgeKing pueden probar pociones sin trabar la investigacion.
                boolean creativeBypass = player.getGameMode() == org.bukkit.GameMode.CREATIVE;
                if (creativeBypass || SlimefunUtils.canPlayerUseItem(player, recipe.getOutput(), true)) {
                    ItemUtils.consumeItem(inv.getItem(matched.ingredientSlot()), true);
                    startAnimation(recipe, inv, block, glassBlocks);
                } else {
                    player.sendMessage("§cDebes investigar esta pocion en la guia de Slimefun antes de prepararla.");
                }
            } else {
                player.sendMessage("§cReceta no valida: pon el polvo de Potion Expansion en el slot superior central (o centro) y de 1 a 3 pociones §emundanas o de agua§c en la fila inferior del dispensador. Investiga la pocion en §e/sf§c primero.");
            }
        }
    }

    public List<AlchemicRecipe> getAlchemicRecipes() {
        return alchemicRecipes;
    }

    @Nullable
    MatchedRecipe checkRecipe(@Nonnull Inventory input) {
        return matchRecipe(alchemicRecipes, input);
    }

    @Nullable
    static MatchedRecipe matchRecipe(@Nonnull List<AlchemicRecipe> candidateRecipes, @Nonnull Inventory input) {
        for (int slot : INGREDIENT_SLOTS) {
            ItemStack ingredient = input.getItem(slot);
            if (ingredient == null) {
                continue;
            }
            for (AlchemicRecipe recipe : candidateRecipes) {
                if (SlimefunUtils.isItemSimilar(ingredient, recipe.getIngredient(), true)
                        && anyValidInputPotion(recipe, input, slot)) {
                    return new MatchedRecipe(recipe, slot);
                }
            }
        }
        return null;
    }

    private List<Block> getWhiteGlassBlocks(@Nonnull Block block) {
        List<Block> list = new ArrayList<>();
        if (block.getRelative(BlockFace.NORTH).getType() == Material.WHITE_STAINED_GLASS) {
            list.add(block.getRelative(BlockFace.NORTH));
        } else if (block.getRelative(BlockFace.EAST).getType() == Material.WHITE_STAINED_GLASS) {
            list.add(block.getRelative(BlockFace.EAST));
        }

        if (block.getRelative(BlockFace.UP).getType() == Material.WHITE_STAINED_GLASS) {
            list.add(block.getRelative(BlockFace.UP));
        }

        if (block.getRelative(BlockFace.SOUTH).getType() == Material.WHITE_STAINED_GLASS) {
            list.add(block.getRelative(BlockFace.SOUTH));
        } else if (block.getRelative(BlockFace.WEST).getType() == Material.WHITE_STAINED_GLASS) {
            list.add(block.getRelative(BlockFace.WEST));
        }

        return list;
    }

    @ParametersAreNonnullByDefault
    private void startAnimation(AlchemicRecipe recipe, Inventory dispInv, Block b, List<Block> glassBlocks) {
        for (int i = 0; i < 10; i++) {
            int j = i;
            PotionExpansion.runSync(() -> {
                World world = b.getWorld();
                if (j < 9) {
                    world.playSound(b.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1f, 2f);
                    glassBlocks.forEach(glassBlock -> {
                        world.playEffect(glassBlock.getLocation().clone().add(0.5, 0, 0.5), Effect.SMOKE, BlockFace.UP);
                    });
                } else {
                    if (anyValidInputPotion(recipe, dispInv, -1)) {
                        world.playSound(b.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1f, 0.1f);
                        brew(recipe, dispInv);
                    } else {
                        world.playSound(b.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_HURT, 1f, 0.5f);
                        world.dropItemNaturally(b.getLocation(), recipe.getIngredient().clone());
                    }
                }
            }, i * 20L);
        }
    }

    @ParametersAreNonnullByDefault
    static void brew(AlchemicRecipe recipe, Inventory dispInv) {
        int brewed = 0;
        for (int slot : POTION_SLOTS) {
            if (brewed >= MAX_POTIONS_PER_BREW) {
                break;
            }
            if (isInputPotionValid(recipe, dispInv.getItem(slot))) {
                dispInv.setItem(slot, recipe.getOutput().clone());
                brewed++;
            }
        }
    }

    @ParametersAreNonnullByDefault
    static boolean anyValidInputPotion(AlchemicRecipe recipe, Inventory dispInv, int skipSlot) {
        for (int slot : POTION_SLOTS) {
            if (slot == skipSlot) {
                continue;
            }
            if (isInputPotionValid(recipe, dispInv.getItem(slot))) {
                return true;
            }
        }
        return false;
    }

    static boolean isInputPotionValid(@Nonnull AlchemicRecipe recipe, @Nullable ItemStack item) {
        if (item == null || item.getType() != Material.POTION) {
            return false;
        }
        if (isCustomSlimefunItem(item)) {
            return false;
        }
        PotionType actualBase = null;
        if (item.hasItemMeta()) {
            if (!(item.getItemMeta() instanceof PotionMeta meta)) {
                return false;
            }
            if (meta.hasCustomEffects()) {
                return false;
            }
            actualBase = meta.getBasePotionType();
        }
        return sameBase(actualBase, recipe.getInputPotion());
    }

    private static boolean isCustomSlimefunItem(@Nonnull ItemStack item) {
        try {
            return SlimefunItem.getByItem(item) != null;
        } catch (Throwable ignored) {
            // In unit tests without a running Slimefun registry, treat mocked items as vanilla
            return false;
        }
    }

    /**
     * Compara la base de dos pociones tratando WATER, MUNDANE y pocion vacia (sin base ni efectos)
     * como equivalentes cuando la receta espera una base vacia.
     */
    static boolean sameBase(@Nullable PotionType actual, @Nullable PotionType expected) {
        if (expected == null || isBlankBase(expected)) {
            return actual == null || isBlankBase(actual);
        }
        return actual == expected;
    }

    static boolean isBlankBase(@Nullable PotionType t) {
        return t == PotionType.WATER || t == PotionType.MUNDANE;
    }
}
