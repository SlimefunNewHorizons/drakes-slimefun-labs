package dev.drake.dough.items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * A legacy-compatible CustomItemStack that extends {@link ItemStack}.
 * This is used heavily in Slimefun and its addons.
 * 
 * @author TheBusyBiscuit
 * @author SlimefunNewHorizons (1.21.1 Port)
 */
public class CustomItemStack extends ItemStack {

    public CustomItemStack(@NotNull ItemStack item) {
        super(item.getType(), item.getAmount());
        this.setItemMeta(item.getItemMeta().clone());
    }

    @Override
    public @NotNull CustomItemStack clone() {
        return new CustomItemStack(this);
    }

    public CustomItemStack(@NotNull Material type) {
        super(type);
    }

    public CustomItemStack(@NotNull ItemStack item, @NotNull Consumer<ItemMeta> consumer) {
        super(item.getType(), item.getAmount());
        this.setItemMeta(item.getItemMeta().clone());
        ItemMeta meta = getItemMeta();
        if (meta != null) {
            consumer.accept(meta);
            setItemMeta(meta);
        }
    }

    public CustomItemStack(@NotNull Material type, @NotNull Consumer<ItemMeta> consumer) {
        this(new ItemStack(type), consumer);
    }

    public CustomItemStack(@NotNull ItemStack item, @Nullable String name, @NotNull String... lore) {
        super(item.getType(), item.getAmount());
        this.setItemMeta(item.getItemMeta().clone());
        ItemMeta meta = getItemMeta();
        if (meta != null) {
            if (name != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            }
            if (lore.length > 0) {
                List<String> lines = new ArrayList<>();
                for (String line : lore) {
                    lines.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                meta.setLore(lines);
            }
            setItemMeta(meta);
        }
    }

    public CustomItemStack(@NotNull Material type, @Nullable String name, @NotNull String... lore) {
        this(new ItemStack(type), name, lore);
    }

    public CustomItemStack(@NotNull ItemStack item, @NotNull List<String> list) {
        this(item, list.isEmpty() ? null : list.get(0), list.isEmpty() ? new String[0] : list.subList(1, list.size()).toArray(new String[0]));
    }

    public CustomItemStack(@NotNull Material type, @NotNull List<String> list) {
        this(new ItemStack(type), list);
    }

    public CustomItemStack(@NotNull Material type, @Nullable String name, @NotNull List<String> lore) {
        this(new ItemStack(type), name, lore.toArray(new String[0]));
    }

    public CustomItemStack(@NotNull ItemStack item, int amount) {
        super(item.getType(), amount);
        this.setItemMeta(item.getItemMeta().clone());
        setAmount(amount);
    }

    @NotNull
    public static CustomItemStack create(@NotNull Material type, @Nullable String name, @NotNull String... lore) {
        return new CustomItemStack(type, name, lore);
    }

    @NotNull
    public static CustomItemStack create(@NotNull Material type, @NotNull List<String> list) {
        return new CustomItemStack(type, list);
    }

    @NotNull
    public static CustomItemStack create(@NotNull Material type, @Nullable String name, @NotNull List<String> lore) {
        return new CustomItemStack(type, name, lore);
    }

    @NotNull
    public static CustomItemStack create(@NotNull Material type, @NotNull Consumer<ItemMeta> consumer) {
        return new CustomItemStack(type, consumer);
    }

    @NotNull
    public static CustomItemStack fromItemStack(@NotNull ItemStack item, @Nullable String name, @NotNull List<String> lore) {
        return new CustomItemStack(item, name, lore.toArray(new String[0]));
    }

}
