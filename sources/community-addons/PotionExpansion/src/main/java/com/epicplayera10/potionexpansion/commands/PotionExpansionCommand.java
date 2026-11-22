package com.epicplayera10.potionexpansion.commands;

import com.epicplayera10.potionexpansion.PotionExpansion;
import com.epicplayera10.potionexpansion.api.effects.EffectsManager;

import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;

public class PotionExpansionCommand implements CommandExecutor {
    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColors.color("&cYou are not a player!"));
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 0) {
            if (args[0].equalsIgnoreCase("showEffects")) {
                if (EffectsManager.hasAnyEffect(player)) {
                    PotionExpansion.getInstance().getEffectsTask().showEffects(player, false);
                } else {
                    player.sendMessage(ChatColors.color("&cYou don't have any effect!"));
                }
            }

            return true;
        }

        return false;
    }
}
