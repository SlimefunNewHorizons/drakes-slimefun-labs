/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.Validate
 */
package me.poma123.globalwarming.api;

import javax.annotation.Nonnull;
import me.poma123.globalwarming.api.TemperatureType;
import org.apache.commons.lang3.Validate;

public class Temperature {
    private double celsiusValue;
    private TemperatureType tempType = TemperatureType.CELSIUS;

    public Temperature(@Nonnull double value) {
        Validate.notNull((Object)value, (String)"The Temperature value should not be null!");
        this.celsiusValue = value;
    }

    public Temperature(@Nonnull double value, @Nonnull TemperatureType type) {
        Validate.notNull((Object)value, (String)"The Temperature value should not be null!");
        Validate.notNull((Object)((Object)type), (String)"The TemperatureType should not be null!");
        this.celsiusValue = value;
        this.tempType = type;
    }

    @Nonnull
    public double getCelsiusValue() {
        return this.celsiusValue;
    }

    @Nonnull
    public double getFahrenheitValue() {
        return this.celsiusValue * 1.8 + 32.0;
    }

    @Nonnull
    public double getKelvinValue() {
        return this.celsiusValue + 273.15;
    }

    @Nonnull
    public double getConvertedValue() {
        switch (this.tempType) {
            case FAHRENHEIT: {
                return this.getFahrenheitValue();
            }
            case KELVIN: {
                return this.getKelvinValue();
            }
        }
        return this.celsiusValue;
    }

    @Nonnull
    public TemperatureType getTemperatureType() {
        return this.tempType;
    }

    public void setTemperatureType(@Nonnull TemperatureType type) {
        Validate.notNull((Object)((Object)type), (String)"The TemperatureType should not be null!");
        this.tempType = type;
    }
}

