/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.drakescraft_labs.slimefun4.api.items.SlimefunItem
 *  com.github.drakescraft_labs.slimefun4.libraries.dough.config.Config
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.Validate
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.event.Event
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package me.poma123.globalwarming.api;

import com.github.drakescraft_labs.slimefun4.api.items.SlimefunItem;
import com.github.drakescraft_labs.slimefun4.libraries.dough.config.Config;
import java.util.Map;
import javax.annotation.Nonnull;
import me.poma123.globalwarming.GlobalWarmingPlugin;
import me.poma123.globalwarming.api.events.AsyncWorldPollutionChangeEvent;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class PollutionManager {
    private static final String DATA_PATH = "data.pollution";

    public static double getPollutionAtLocation(@Nonnull Location loc) {
        Validate.notNull((Object)loc, (String)"The Location should not be null!");
        return PollutionManager.getPollutionInWorld(loc.getWorld());
    }

    public static double getPollutionInWorld(@Nonnull World world) {
        Config config;
        Validate.notNull((Object)world, (String)"The World should not be null!");
        if (GlobalWarmingPlugin.getRegistry().isWorldEnabled(world.getName()) && (config = GlobalWarmingPlugin.getRegistry().getWorldConfig(world)) != null) {
            return config.getDouble(DATA_PATH);
        }
        return 0.0;
    }

    public static boolean risePollutionInWorld(@Nonnull World world, @Nonnull double value) {
        Config config;
        Validate.notNull((Object)world, (String)"The World should not be null!");
        Validate.notNull((Object)world, (String)"The pollution value should not be null!");
        if (GlobalWarmingPlugin.getRegistry().isWorldEnabled(world.getName()) && (config = GlobalWarmingPlugin.getRegistry().getWorldConfig(world)) != null) {
            double oldValue = config.getDouble(DATA_PATH);
            value = oldValue + value;
            AsyncWorldPollutionChangeEvent event = new AsyncWorldPollutionChangeEvent(world, oldValue, value);
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)GlobalWarmingPlugin.getInstance(), () -> Bukkit.getPluginManager().callEvent((Event)event));
            config.setValue(DATA_PATH, (Object)value);
            config.save();
            return true;
        }
        return false;
    }

    public static boolean descendPollutionInWorld(@Nonnull World world, @Nonnull double value) {
        Config config;
        Validate.notNull((Object)world, (String)"The World should not be null!");
        Validate.notNull((Object)world, (String)"The pollution value should not be null!");
        if (GlobalWarmingPlugin.getRegistry().isWorldEnabled(world.getName()) && (config = GlobalWarmingPlugin.getRegistry().getWorldConfig(world)) != null) {
            double oldValue = config.getDouble(DATA_PATH);
            value = Math.max(oldValue - value, 0.0);
            AsyncWorldPollutionChangeEvent event = new AsyncWorldPollutionChangeEvent(world, oldValue, value);
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)GlobalWarmingPlugin.getInstance(), () -> Bukkit.getPluginManager().callEvent((Event)event));
            config.setValue(DATA_PATH, (Object)value);
            config.save();
            return true;
        }
        return false;
    }

    public static boolean setPollutionInWorld(@Nonnull World world, @Nonnull double newValue) {
        Config config;
        Validate.notNull((Object)world, (String)"The World should not be null!");
        Validate.notNull((Object)world, (String)"The pollution value should not be null!");
        if (GlobalWarmingPlugin.getRegistry().isWorldEnabled(world.getName()) && (config = GlobalWarmingPlugin.getRegistry().getWorldConfig(world)) != null) {
            double oldValue = config.getDouble(DATA_PATH);
            AsyncWorldPollutionChangeEvent event = new AsyncWorldPollutionChangeEvent(world, oldValue, newValue);
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)GlobalWarmingPlugin.getInstance(), () -> Bukkit.getPluginManager().callEvent((Event)event));
            config.setValue(DATA_PATH, (Object)newValue);
            config.save();
            return true;
        }
        return false;
    }

    public static double isPollutedItem(@Nonnull ItemStack item) {
        Validate.notNull((Object)item, (String)"The ItemStack should not be null!");
        SlimefunItem sfItem = SlimefunItem.getByItem((ItemStack)item);
        Map<String, Double> pollutedSlimefunItems = GlobalWarmingPlugin.getRegistry().getPollutedSlimefunItems();
        Map<Material, Double> pollutedVanillaItems = GlobalWarmingPlugin.getRegistry().getPollutedVanillaItems();
        if (sfItem != null && pollutedSlimefunItems.containsKey(sfItem.getId())) {
            return pollutedSlimefunItems.get(sfItem.getId());
        }
        if (pollutedVanillaItems.containsKey(item.getType())) {
            return pollutedVanillaItems.get(item.getType());
        }
        return 0.0;
    }

    public static double isPollutedMachine(@Nonnull String id) {
        Validate.notNull((Object)id, (String)"The Id should not be null!");
        SlimefunItem sfItem = SlimefunItem.getById((String)id);
        Map<String, Double> pollutedSlimefunMachines = GlobalWarmingPlugin.getRegistry().getPollutedSlimefunMachines();
        if (sfItem != null && pollutedSlimefunMachines.containsKey(sfItem.getId())) {
            return pollutedSlimefunMachines.get(sfItem.getId());
        }
        return 0.0;
    }

    public static double isAbsorbentMachine(@Nonnull String id) {
        Validate.notNull((Object)id, (String)"The Id should not be null!");
        SlimefunItem sfItem = SlimefunItem.getById((String)id);
        Map<String, Double> absorbentSlimefunMachines = GlobalWarmingPlugin.getRegistry().getAbsorbentSlimefunMachines();
        if (sfItem != null && absorbentSlimefunMachines.containsKey(sfItem.getId())) {
            return absorbentSlimefunMachines.get(sfItem.getId());
        }
        return 0.0;
    }
}

