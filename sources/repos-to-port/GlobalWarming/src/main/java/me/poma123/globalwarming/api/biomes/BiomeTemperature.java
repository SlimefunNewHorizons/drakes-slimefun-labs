/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.Validate
 */
package me.poma123.globalwarming.api.biomes;

import javax.annotation.Nonnull;
import org.apache.commons.lang3.Validate;

public class BiomeTemperature {
    private final double temperature;
    private final double maxTemperatureDropAtNight;

    @Nonnull
    public BiomeTemperature(@Nonnull double temperature, @Nonnull double maxTemperatureDropAtNight) {
        Validate.notNull((Object)temperature, (String)"The temperature value should not be null!");
        Validate.notNull((Object)maxTemperatureDropAtNight, (String)"The maxTemperatureDropAtNight value should not be null!");
        this.temperature = temperature;
        this.maxTemperatureDropAtNight = maxTemperatureDropAtNight;
    }

    @Nonnull
    public double getTemperature() {
        return this.temperature;
    }

    @Nonnull
    public double getMaxTemperatureDropAtNight() {
        return this.maxTemperatureDropAtNight;
    }
}

