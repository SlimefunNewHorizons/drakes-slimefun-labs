package dev.drake.dough.protection.modules;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import dev.drake.dough.protection.Interaction;
import dev.drake.dough.protection.ProtectionModule;

/**
 * Robust reflection-based BentoBox protection module for SkyBlock and OneBlock.
 * Ensures Slimefun machines, containers, and blocks cannot be accessed without island permissions.
 *
 * @author SlimefunNewHorizons
 */
public class BentoBoxProtectionModule implements ProtectionModule {

    private final Plugin plugin;
    private Logger logger;

    private Object islandsManager;
    private Object islandWorldManager;

    private Method inWorldLocationMethod;
    private Method getIslandAtMethod;
    private Method userGetInstanceMethod;
    private Method isAllowedMethod;
    private Method isSetForWorldMethod;

    private Object flagContainer;
    private Object flagBreakBlocks;
    private Object flagPlaceBlocks;
    private Object flagHurtAnimals;
    private Object flagPvpOverworld;
    private Object flagPvpNether;
    private Object flagPvpEnd;

    private boolean initialized = false;

    public BentoBoxProtectionModule(@Nonnull Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void load() {
        this.logger = plugin.getLogger();
        try {
            Class<?> bentoBoxClass = Class.forName("world.bentobox.bentobox.BentoBox");
            Object bentoInstance = bentoBoxClass.getMethod("getInstance").invoke(null);

            this.islandsManager = bentoBoxClass.getMethod("getIslands").invoke(bentoInstance);
            this.islandWorldManager = bentoBoxClass.getMethod("getIWM").invoke(bentoInstance);

            this.inWorldLocationMethod = islandWorldManager.getClass().getMethod("inWorld", Location.class);
            this.getIslandAtMethod = islandsManager.getClass().getMethod("getIslandAt", Location.class);

            Class<?> userClass = Class.forName("world.bentobox.bentobox.api.user.User");
            try {
                this.userGetInstanceMethod = userClass.getMethod("getInstance", OfflinePlayer.class);
            } catch (NoSuchMethodException e) {
                this.userGetInstanceMethod = userClass.getMethod("getInstance", UUID.class);
            }

            Class<?> flagClass = Class.forName("world.bentobox.bentobox.api.flags.Flag");
            Class<?> islandClass = Class.forName("world.bentobox.bentobox.database.objects.Island");

            this.isAllowedMethod = islandClass.getMethod("isAllowed", userClass, flagClass);
            this.isSetForWorldMethod = flagClass.getMethod("isSetForWorld", World.class);

            Class<?> flagsClass = Class.forName("world.bentobox.bentobox.lists.Flags");
            this.flagContainer = flagsClass.getField("CONTAINER").get(null);
            this.flagBreakBlocks = flagsClass.getField("BREAK_BLOCKS").get(null);
            this.flagPlaceBlocks = flagsClass.getField("PLACE_BLOCKS").get(null);
            this.flagHurtAnimals = flagsClass.getField("HURT_ANIMALS").get(null);

            try {
                this.flagPvpOverworld = flagsClass.getField("PVP_OVERWORLD").get(null);
            } catch (NoSuchFieldException ignored) {}
            try {
                this.flagPvpNether = flagsClass.getField("PVP_NETHER").get(null);
            } catch (NoSuchFieldException ignored) {}
            try {
                this.flagPvpEnd = flagsClass.getField("PVP_END").get(null);
            } catch (NoSuchFieldException ignored) {}

            this.initialized = true;
            logger.info("[Protection] BentoBoxProtectionModule cargado exitosamente via reflection (SkyBlock/OneBlock activo).");
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "[Protection] Error inicializando BentoBoxProtectionModule via reflection", t);
            this.initialized = false;
        }
    }

    @Override
    public boolean hasPermission(OfflinePlayer p, Location l, Interaction action) {
        if (!initialized || l == null || l.getWorld() == null || p == null) {
            return true;
        }

        try {
            boolean inBentoWorld = (boolean) inWorldLocationMethod.invoke(islandWorldManager, l);
            if (!inBentoWorld) {
                // No es mundo de BentoBox (es survival, clasico, etc.): no interferir
                return true;
            }

            Object flag = resolveFlag(action, l.getWorld());
            if (flag == null) {
                return true;
            }

            @SuppressWarnings("unchecked")
            Optional<?> optIsland = (Optional<?>) getIslandAtMethod.invoke(islandsManager, l);

            if (optIsland != null && optIsland.isPresent()) {
                Object island = optIsland.get();
                Object user = userGetInstanceMethod.getParameterTypes()[0].equals(UUID.class)
                        ? userGetInstanceMethod.invoke(null, p.getUniqueId())
                        : userGetInstanceMethod.invoke(null, p);

                return (boolean) isAllowedMethod.invoke(island, user, flag);
            } else {
                // Fuera de una isla en mundo BentoBox (spawn o vacio)
                return (boolean) isSetForWorldMethod.invoke(flag, l.getWorld());
            }
        } catch (Throwable t) {
            logger.log(Level.WARNING, "[Protection] Error consultando permisos en BentoBox para " + p.getName() + " en " + l, t);
            return false; // Denegar por seguridad ante fallo interno en isla
        }
    }

    private Object resolveFlag(Interaction action, World world) {
        switch (action) {
            case INTERACT_BLOCK:
                // Acceder/abrir maquinas de Slimefun requiere permiso de contenedor (Flags.CONTAINER)
                return flagContainer != null ? flagContainer : flagBreakBlocks;
            case BREAK_BLOCK:
                return flagBreakBlocks;
            case ATTACK_ENTITY:
                return flagHurtAnimals;
            case ATTACK_PLAYER:
                if (world.getEnvironment() == World.Environment.NETHER && flagPvpNether != null) {
                    return flagPvpNether;
                } else if (world.getEnvironment() == World.Environment.THE_END && flagPvpEnd != null) {
                    return flagPvpEnd;
                } else if (flagPvpOverworld != null) {
                    return flagPvpOverworld;
                }
                return null;
            case PLACE_BLOCK:
            default:
                return flagPlaceBlocks;
        }
    }
}
