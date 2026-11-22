package com.epicplayera10.potionexpansion;

import com.epicplayera10.potionexpansion.items.PotionItems;

import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.NamespacedKey;

import javax.annotation.Nonnull;

public class ResearchSetup {
    public static void setup(@Nonnull PotionExpansion plugin) {
        Research coalSightResearch = new Research(new NamespacedKey(plugin, "coal_sight_research"),
                74100, "CoalSight Research", 10);
        coalSightResearch.addItems(PotionItems.COAL_SIGHT);
        coalSightResearch.register();

        Research ironSightResearch = new Research(new NamespacedKey(plugin, "iron_sight_research"),
                74101, "IronSight Research", 10);
        ironSightResearch.addItems(PotionItems.IRON_SIGHT);
        ironSightResearch.register();

        Research diamondSightResearch = new Research(new NamespacedKey(plugin, "diamond_sight_research"),
                74102, "DiamondSight Research", 10);
        diamondSightResearch.addItems(PotionItems.DIAMOND_SIGHT);
        diamondSightResearch.register();

        Research goldSightResearch = new Research(new NamespacedKey(plugin, "gold_sight_research"),
                74103, "GoldSight Research", 10);
        goldSightResearch.addItems(PotionItems.GOLD_SIGHT);
        goldSightResearch.register();

        Research lapisSightResearch = new Research(new NamespacedKey(plugin, "lapis_sight_research"),
                74104, "LapisSight Research", 10);
        lapisSightResearch.addItems(PotionItems.LAPIS_SIGHT);
        lapisSightResearch.register();

        Research redstoneSightResearch = new Research(new NamespacedKey(plugin, "redstone_sight_research"),
                74105, "RedstoneSight Research", 10);
        redstoneSightResearch.addItems(PotionItems.REDSTONE_SIGHT);
        redstoneSightResearch.register();

        Research emeraldSightResearch = new Research(new NamespacedKey(plugin, "emerald_sight_research"),
                74106, "EmeraldSight Research", 10);
        emeraldSightResearch.addItems(PotionItems.EMERALD_SIGHT);
        emeraldSightResearch.register();

        Research quartzSightResearch = new Research(new NamespacedKey(plugin, "quartz_sight_research"),
                74107, "QuartzSight Research", 10);
        quartzSightResearch.addItems(PotionItems.QUARTZ_SIGHT);
        quartzSightResearch.register();

        MinecraftVersion minecraftVersion = Slimefun.getMinecraftVersion();

        if (minecraftVersion.isAtLeast(MinecraftVersion.MINECRAFT_1_16)) {
            Research ancientDebrisSightResearch = new Research(new NamespacedKey(plugin, "ancient_debris_sight_research"),
                    74108, "AncientDebrisSight Research", 10);
            ancientDebrisSightResearch.addItems(PotionItems.ANCIENT_DEBRIS_SIGHT);
            ancientDebrisSightResearch.register();
        }

        if (minecraftVersion.isAtLeast(MinecraftVersion.MINECRAFT_1_17)) {
            Research copperSightResearch = new Research(new NamespacedKey(plugin, "copper_sight_research"),
                    74109, "CopperSight Research", 10);
            copperSightResearch.addItems(PotionItems.COPPER_SIGHT);
            copperSightResearch.register();
        }

        Research pestleResearch = new Research(new NamespacedKey(plugin, "pestle_research"),
                74110, "Pestle Research", 5);
        pestleResearch.addItems(PotionItems.PESTLE);
        pestleResearch.register();

        plugin.getLogger().info("Loaded researches!");
    }
}
