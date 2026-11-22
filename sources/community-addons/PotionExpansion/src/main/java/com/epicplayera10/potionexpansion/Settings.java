package com.epicplayera10.potionexpansion;

import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;

import javax.annotation.Nonnull;

public class Settings {
    private static int potionDuration;
    private static int searchRadius;

    public static void load(@Nonnull Config cfg) {
        potionDuration = cfg.getInt("potions-sight.duration");
        searchRadius = cfg.getInt("potions-sight.radius");
    }

    public static int getPotionDuration() {
        return potionDuration;
    }

    public static int getSearchRadius() {
        return searchRadius;
    }
}
