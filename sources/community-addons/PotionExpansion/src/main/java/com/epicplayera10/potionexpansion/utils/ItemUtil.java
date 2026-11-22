package com.epicplayera10.potionexpansion.utils;

import com.epicplayera10.potionexpansion.Settings;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.Collections;

public class ItemUtil {
    public static SlimefunItemStack createCustomPotionItem(String id, String name, Color color) {
        return new SlimefunItemStack(id,
                Material.POTION,
                name,
                itemMeta -> {
                    PotionMeta meta = (PotionMeta) itemMeta;
                    meta.setColor(color);
                    meta.addEnchant(Enchantment.LURE, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                    meta.setLore(Collections.singletonList(ChatColor.BLUE + ChatUtils.humanize(id) + " (" + formatTime(Settings.getPotionDuration()) + ")"));
                }
        );
    }

    private static String formatTime(long secs) {
        return String.format("%d:%02d", (secs % 3600) / 60, secs % 60);
    }
}
