/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.drakescraft_labs.slimefun4.implementation.Slimefun
 *  com.github.drakescraft_labs.slimefun4.libraries.dough.common.ChatColors
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.Validate
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerCommandPreprocessEvent
 *  org.bukkit.plugin.Plugin
 */
package me.poma123.globalwarming.commands;

import com.github.drakescraft_labs.slimefun4.implementation.Slimefun;
import com.github.drakescraft_labs.slimefun4.libraries.dough.common.ChatColors;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import me.poma123.globalwarming.GlobalWarmingPlugin;
import me.poma123.globalwarming.commands.SubCommand;
import me.poma123.globalwarming.commands.subcommands.SubCommands;
import org.apache.commons.lang3.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

public class GlobalWarmingCommand
implements CommandExecutor,
Listener {
    private boolean registered = false;
    private final GlobalWarmingPlugin plugin;
    private final List<SubCommand> commands = new LinkedList<SubCommand>();

    public GlobalWarmingCommand(@Nonnull GlobalWarmingPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Validate.isTrue((!this.registered ? 1 : 0) != 0, (String)"Comandos de GlobalWarming registrados.");
        this.registered = true;
        this.plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this.plugin);
        this.plugin.getCommand("globalwarming").setExecutor((CommandExecutor)this);
        this.commands.addAll(SubCommands.getAllCommands(this));
    }

    @Nonnull
    public GlobalWarmingPlugin getPlugin() {
        return this.plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            for (SubCommand command : this.commands) {
                if (!args[0].equalsIgnoreCase(command.getName())) continue;
                command.onExecute(sender, args);
                return true;
            }
        }
        this.sendHelp(sender);
        return !this.commands.isEmpty();
    }

    public void sendHelp(@Nonnull CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(ChatColors.color((String)("&aCalentamiento Global &2v" + Slimefun.getVersion())));
        sender.sendMessage("");
        for (SubCommand cmd : this.commands) {
            if (cmd.isHidden()) continue;
            sender.sendMessage(ChatColors.color((String)("&3/globalwarming " + cmd.getName() + " &b")) + cmd.getDescription());
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().equalsIgnoreCase("/help globalwarming")) {
            this.sendHelp((CommandSender)e.getPlayer());
            e.setCancelled(true);
        }
    }

    @Nonnull
    public List<String> getSubCommandNames() {
        return this.commands.stream().map(SubCommand::getName).collect(Collectors.toList());
    }
}

