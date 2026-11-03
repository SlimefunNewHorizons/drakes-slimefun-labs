package dev.drake.dough.protection;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import dev.drake.dough.common.DoughLogger;
import dev.drake.dough.protection.loggers.CoreProtectLogger;
import dev.drake.dough.protection.modules.BentoBoxProtectionModule;
import dev.drake.dough.protection.modules.WorldGuardProtectionModule;

/**
 * This Class provides a nifty API for plugins to query popular protection plugins.
 * [Minimalist version for 1.21.1 Migration]
 * 
 * @author TheBusyBiscuit
 * @author SlimefunNewHorizons (Purge)
 */
public final class ProtectionManager {

    private final Set<ProtectionModule> protectionModules = new HashSet<>();
    private final Set<ProtectionLogger> protectionLoggers = new HashSet<>();
    private final Logger logger;

    public ProtectionManager(@Nonnull Plugin plugin) {
        this.logger = new DoughLogger(plugin.getServer(), "protection");

        logger.log(Level.INFO, "Loading Protection Modules (Minimalist Build)...");

        loadModuleImplementations(plugin);
        loadLoggerImplementations(plugin);
    }

    @ParametersAreNonnullByDefault
    private void loadModuleImplementations(Plugin plugin) {
        PluginManager pm = plugin.getServer().getPluginManager();

        registerModule(pm, "WorldGuard", worldGuard -> new WorldGuardProtectionModule(worldGuard));
        registerModule(pm, "BentoBox", bentoBox -> new BentoBoxProtectionModule(bentoBox));
    }

    @ParametersAreNonnullByDefault
    private void loadLoggerImplementations(Plugin plugin) {
        PluginManager pm = plugin.getServer().getPluginManager();

        if (pm.isPluginEnabled("CoreProtect")) {
            registerLogger(new CoreProtectLogger());
        }
    }

    @ParametersAreNonnullByDefault
    public void registerLogger(String name, ProtectionLogger module) {
        protectionLoggers.add(module);
        logger.log(Level.INFO, "Loaded Protection Logger \"{0}\"", name);
    }

    @ParametersAreNonnullByDefault
    public void registerModule(PluginManager pm, String pluginName, Function<Plugin, ProtectionModule> constructor) {
        Plugin plugin = pm.getPlugin(pluginName);

        if (plugin != null && plugin.isEnabled()) {
            registerModule(plugin, constructor);
        }
    }

    @ParametersAreNonnullByDefault
    private void registerModule(Plugin plugin, Function<Plugin, ProtectionModule> constructor) {
        try {
            ProtectionModule module = constructor.apply(plugin);
            module.load();

            protectionModules.add(module);
            logger.log(Level.INFO, "Loaded Protection Module \"{0}\" v{1}", new Object[] {module.getName(), module.getVersion()});
        } catch (Throwable x) {
            logger.log(Level.SEVERE, x, () -> "An Error occured while registering the Protection Module: \"" + plugin.getName() + "\" v" + plugin.getDescription().getVersion());
        }
    }

    @ParametersAreNonnullByDefault
    public void registerLogger(ProtectionLogger module) {
        try {
            module.load();
            registerLogger(module.getName(), module);
        } catch (Throwable x) {
            logger.log(Level.SEVERE, x, () -> "An Error occured while registering the Protection Module: \"" + module.getName() + "\"");
        }
    }

    @ParametersAreNonnullByDefault
    public boolean hasPermission(OfflinePlayer p, Block b, Interaction action) {
        return hasPermission(p, b.getLocation(), action);
    }

    @ParametersAreNonnullByDefault
    public boolean hasPermission(OfflinePlayer p, Location l, Interaction action) {
        for (ProtectionModule module : protectionModules) {
            try {
                if (!module.hasPermission(p, l, action)) {
                    return false;
                }
            } catch (Exception | LinkageError x) {
                logger.log(Level.SEVERE, x, () -> "An Error occured while querying the Protection Module: \"" + module.getName() + " v" + module.getVersion() + "\"");
                return true;
            }
        }
        return true;
    }

    @ParametersAreNonnullByDefault
    public void logAction(OfflinePlayer p, Block b, Interaction action) {
        for (ProtectionLogger module : protectionLoggers) {
            try {
                module.logAction(p, b, action);
            } catch (Exception | LinkageError x) {
                logger.log(Level.SEVERE, x, () -> "An Error occured while logging for the Protection Module: \"" + module.getName() + "\"");
            }
        }
    }

}
