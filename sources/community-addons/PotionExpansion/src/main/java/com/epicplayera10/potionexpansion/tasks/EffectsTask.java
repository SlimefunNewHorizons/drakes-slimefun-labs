package com.epicplayera10.potionexpansion.tasks;

import com.epicplayera10.potionexpansion.PotionExpansion;
import com.epicplayera10.potionexpansion.Settings;
import com.epicplayera10.potionexpansion.api.effects.PotionSightEffect;
import com.epicplayera10.potionexpansion.api.effects.PotionSightType;
import com.epicplayera10.potionexpansion.api.effects.EffectsManager;
import com.epicplayera10.potionexpansion.utils.XRayUtil;

import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EffectsTask extends BukkitRunnable {

    public EffectsTask(@Nonnull PotionExpansion plugin) {
        this.runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (EffectsManager.hasAnyEffect(player)) {

                List<PotionSightEffect> effects = EffectsManager.getPlayerEffects(player);

                effects.forEach(effect -> effect.setTime(effect.getTime() - 1));

                showEffects(player, true);

                tick(player, effects);

                effects.removeIf(effect -> effect.getTime() <= 0);

                if (effects.size() == 0) {
                    EffectsManager.removePlayerEffects(player.getUniqueId());
                }
            }
        }
    }

    public void showEffects(@Nonnull Player player, boolean actionBar) {
        StringBuilder message = new StringBuilder();
        message.append(ChatColors.color("&aEffects: &e"));

        boolean first = true;
        for (PotionSightEffect potionSightEffect : EffectsManager.getPlayerEffects(player)) {
            SimpleDateFormat formatter = new SimpleDateFormat("mm'm' ss's'");
            String formattedTime = formatter.format(new Date(potionSightEffect.getTime() * 1000L));
            if (first) {
                message.append(ChatUtils.humanize(potionSightEffect.getType().toString())+" ");
                message.append(ChatColors.color("&aTime: &e"+formattedTime));
                first = false;
            } else {
                message.append(ChatColors.color("&a, &e"+ChatUtils.humanize(potionSightEffect.getType().toString())+" "));
                message.append(ChatColors.color("&aTime: &e"+formattedTime));
            }
        }

        if (actionBar) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message.toString()));
        } else {
            player.sendMessage(message.toString());
        }
    }

    @ParametersAreNonnullByDefault
    private void tick(Player player, List<PotionSightEffect> effects) {
        for (PotionSightEffect effect : effects) {
            PotionSightType type = effect.getType();

            for (Material ore : type.getOres()) {
                if (ore != null) {
                    XRayUtil.showPathsToMaterial(player, ore, type.getColor(), Settings.getSearchRadius());
                }
            }
        }
    }
}
