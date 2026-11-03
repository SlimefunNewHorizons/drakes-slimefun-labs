/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.drakescraft_labs.slimefun4.api.SlimefunAddon
 *  com.github.drakescraft_labs.slimefun4.api.items.ItemGroup
 *  com.github.drakescraft_labs.slimefun4.api.items.SlimefunItem
 *  com.github.drakescraft_labs.slimefun4.api.recipes.RecipeType
 *  com.github.drakescraft_labs.slimefun4.api.researches.Research
 *  com.github.drakescraft_labs.slimefun4.core.handlers.ItemConsumptionHandler
 *  com.github.drakescraft_labs.slimefun4.implementation.SlimefunItems
 *  com.github.drakescraft_labs.slimefun4.implementation.items.SimpleSlimefunItem
 *  com.github.drakescraft_labs.slimefun4.legacy.api.BlockStorage
 *  com.github.drakescraft_labs.slimefun4.libraries.dough.config.Config
 *  com.github.drakescraft_labs.slimefun4.libraries.dough.items.CustomItemStack
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.block.Block
 *  org.bukkit.event.Listener
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.poma123.globalwarming;

import com.github.drakescraft_labs.slimefun4.api.SlimefunAddon;
import com.github.drakescraft_labs.slimefun4.api.items.ItemGroup;
import com.github.drakescraft_labs.slimefun4.api.items.SlimefunItem;
import com.github.drakescraft_labs.slimefun4.api.recipes.RecipeType;
import com.github.drakescraft_labs.slimefun4.api.researches.Research;
import com.github.drakescraft_labs.slimefun4.core.handlers.ItemConsumptionHandler;
import com.github.drakescraft_labs.slimefun4.implementation.SlimefunItems;
import com.github.drakescraft_labs.slimefun4.implementation.items.SimpleSlimefunItem;
import com.github.drakescraft_labs.slimefun4.legacy.api.BlockStorage;
import com.github.drakescraft_labs.slimefun4.libraries.dough.config.Config;
import com.github.drakescraft_labs.slimefun4.libraries.dough.items.CustomItemStack;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.EnumMap;
import java.util.logging.Level;
import me.poma123.globalwarming.Items;
import me.poma123.globalwarming.Registry;
import me.poma123.globalwarming.TemperatureManager;
import me.poma123.globalwarming.api.TemperatureType;
import me.poma123.globalwarming.commands.GlobalWarmingCommand;
import me.poma123.globalwarming.eventos.EventoClimatico;
import me.poma123.globalwarming.eventos.GestorEventos;
import me.poma123.globalwarming.eventos.Silenciados;
import me.poma123.globalwarming.items.CinnabariteResource;
import me.poma123.globalwarming.items.machines.AirCompressor;
import me.poma123.globalwarming.items.machines.Climatizador;
import me.poma123.globalwarming.items.machines.SumideroCarbono;
import me.poma123.globalwarming.items.machines.TemperatureMeter;
import me.poma123.globalwarming.listeners.PollutionListener;
import me.poma123.globalwarming.listeners.WorldListener;
import me.poma123.globalwarming.tasks.BurnTask;
import me.poma123.globalwarming.tasks.FireTask;
import me.poma123.globalwarming.tasks.MeltTask;
import me.poma123.globalwarming.tasks.SlownessTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class GlobalWarmingPlugin
extends JavaPlugin
implements SlimefunAddon {
    private static GlobalWarmingPlugin instance;
    private static Registry registry;
    private static GestorEventos gestorEventos;
    private static Silenciados silenciados;
    private final TemperatureManager temperatureManager = new TemperatureManager();
    private final GlobalWarmingCommand command = new GlobalWarmingCommand(this);
    private final Config cfg = new Config((Plugin)this);
    private Config messages;

    public void onEnable() {
        File post118BiomeMap;
        File pre118BiomeMap;
        File messagesFile;
        instance = this;
        if (!this.getConfig().getBoolean("options.auto-update") || this.getDescription().getVersion().startsWith("Build")) {
            // empty if block
        }
        if (!(messagesFile = new File(this.getDataFolder(), "messages.yml")).exists()) {
            try {
                Files.copy(((Object)((Object)this)).getClass().getResourceAsStream("/messages.yml"), messagesFile.toPath(), new CopyOption[0]);
            }
            catch (IOException e) {
                this.getLogger().log(Level.SEVERE, "No se pudo crear la configuraci\u00f3n messages.yml", e);
            }
        }
        this.messages = new Config((Plugin)this, "messages.yml");
        File biomeMapDirectory = new File(this.getDataFolder(), "biome-maps");
        if (!biomeMapDirectory.exists()) {
            biomeMapDirectory.mkdirs();
        }
        if (!(pre118BiomeMap = new File(biomeMapDirectory, "pre-1.18.json")).exists()) {
            try {
                Files.copy(((Object)((Object)this)).getClass().getResourceAsStream("/biome-maps/pre-1.18.json"), pre118BiomeMap.toPath(), new CopyOption[0]);
            }
            catch (IOException e) {
                this.getLogger().log(Level.SEVERE, "No se pudo crear la configuraci\u00f3n biome-maps/pre-1.18.json", e);
            }
        }
        if (!(post118BiomeMap = new File(biomeMapDirectory, "post-1.18.json")).exists()) {
            try {
                Files.copy(((Object)((Object)this)).getClass().getResourceAsStream("/biome-maps/post-1.18.json"), post118BiomeMap.toPath(), new CopyOption[0]);
            }
            catch (IOException e) {
                this.getLogger().log(Level.SEVERE, "No se pudo crear la configuraci\u00f3n biome-maps/post-1.18.json", e);
            }
        }
        this.registerItems();
        this.registerResearches();
        registry.load(this.cfg, this.messages);
        silenciados = new Silenciados(this);
        this.scheduleTasks();
        this.command.register();
        Bukkit.getPluginManager().registerEvents((Listener)new PollutionListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new WorldListener(), (Plugin)this);
    }

    private void registerItems() {
        ItemGroup itemGroup = new ItemGroup(new NamespacedKey((Plugin)this, "global_warming"), (ItemStack)new CustomItemStack((ItemStack)Items.THERMOMETER, "&2Calentamiento Global", new String[0]));
        new TemperatureMeter(itemGroup, Items.THERMOMETER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{SlimefunItems.NICKEL_INGOT, new ItemStack(Material.GLASS), SlimefunItems.NICKEL_INGOT, SlimefunItems.NICKEL_INGOT, Items.MERCURY, SlimefunItems.NICKEL_INGOT, SlimefunItems.NICKEL_INGOT, new ItemStack(Material.GLASS), SlimefunItems.NICKEL_INGOT}){

            @Override
            public void tick(Block b) {
                Location loc = b.getLocation();
                this.updateHologram(b, GlobalWarmingPlugin.getTemperatureManager().getTemperatureString(loc, TemperatureType.valueOf(BlockStorage.getLocationInfo((Location)loc, (String)"type"))));
            }
        }.register(this);
        new TemperatureMeter(itemGroup, Items.AIR_QUALITY_METER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{SlimefunItems.BILLON_INGOT, SlimefunItems.BILLON_INGOT, SlimefunItems.BILLON_INGOT, SlimefunItems.SOLDER_INGOT, Items.THERMOMETER, SlimefunItems.SOLDER_INGOT, SlimefunItems.SOLDER_INGOT, SlimefunItems.MAGNET, SlimefunItems.SOLDER_INGOT}){

            @Override
            public void tick(Block b) {
                Location loc = b.getLocation();
                this.updateHologram(b, "&7Cambio del entorno: " + GlobalWarmingPlugin.getTemperatureManager().getAirQualityString(loc.getWorld(), TemperatureType.valueOf(BlockStorage.getLocationInfo((Location)loc, (String)"type"))));
            }
        }.register(this);
        new AirCompressor(itemGroup, Items.AIR_COMPRESSOR, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{SlimefunItems.SOLDER_INGOT, Items.FILTER, SlimefunItems.SOLDER_INGOT, SlimefunItems.ALUMINUM_BRASS_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.ALUMINUM_BRASS_INGOT, SlimefunItems.SOLDER_INGOT, SlimefunItems.BATTERY, SlimefunItems.SOLDER_INGOT}){

            public int getEnergyConsumption() {
                return 16;
            }

            public int getCapacity() {
                return 512;
            }

            public int getSpeed() {
                return 1;
            }
        }.register(this);
        new SlimefunItem(itemGroup, Items.EMPTY_CANISTER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{null, SlimefunItems.SOLDER_INGOT, null, SlimefunItems.SOLDER_INGOT, new ItemStack(Material.GLASS_BOTTLE), SlimefunItems.SOLDER_INGOT, SlimefunItems.SOLDER_INGOT, SlimefunItems.SOLDER_INGOT, SlimefunItems.SOLDER_INGOT}).register((SlimefunAddon)this);
        new SimpleSlimefunItem<ItemConsumptionHandler>(itemGroup, Items.CO2_CANISTER, AirCompressor.RECIPE_TYPE, new ItemStack[]{null, null, null, null, Items.EMPTY_CANISTER, null, null, null, null}){

            public ItemConsumptionHandler getItemHandler() {
                return (e, p, item) -> e.setCancelled(true);
            }
        }.register((SlimefunAddon)this);
        new SlimefunItem(itemGroup, Items.CINNABARITE, RecipeType.GEO_MINER, new ItemStack[0]).register((SlimefunAddon)this);
        new CinnabariteResource().register();
        new SlimefunItem(itemGroup, Items.MERCURY, RecipeType.SMELTERY, new ItemStack[]{Items.CINNABARITE, null, null, null, null, null, null, null, null}).register((SlimefunAddon)this);
        new SlimefunItem(itemGroup, Items.FILTER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{null, new ItemStack(Material.GLASS), null, new ItemStack(Material.GLASS), SlimefunItems.GOLD_PAN, new ItemStack(Material.GLASS), null, new ItemStack(Material.GLASS), null}).register((SlimefunAddon)this);
        new Climatizador(itemGroup, Items.CLIMATIZADOR, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{SlimefunItems.ALUMINUM_BRASS_INGOT, Items.FILTER, SlimefunItems.ALUMINUM_BRASS_INGOT, SlimefunItems.COOLING_UNIT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.HEATING_COIL, SlimefunItems.ALUMINUM_BRASS_INGOT, SlimefunItems.BATTERY, SlimefunItems.ALUMINUM_BRASS_INGOT}, 24, 512).register(this);
        new SumideroCarbono(itemGroup, Items.SUMIDERO_CARBONO, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{SlimefunItems.REINFORCED_ALLOY_INGOT, Items.CO2_CANISTER, SlimefunItems.REINFORCED_ALLOY_INGOT, new ItemStack(Material.DEEPSLATE), SlimefunItems.CARBON_PRESS_3, new ItemStack(Material.DEEPSLATE), SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.BATTERY, SlimefunItems.REINFORCED_ALLOY_INGOT}){

            public int getEnergyConsumption() {
                return 32;
            }

            public int getCapacity() {
                return 512;
            }

            public int getSpeed() {
                return 1;
            }
        }.register(this);
    }

    private void registerResearches() {
        this.registerResearch("thermometer", 69696969, "Term\u00f3metro", 10, new ItemStack[]{Items.THERMOMETER});
        this.registerResearch("air_quality_meter", 69696970, "Medidor de Calidad del Aire", 30, new ItemStack[]{Items.AIR_QUALITY_METER});
        this.registerResearch("air_compressor", 69696971, "Compresor de Aire", 40, new ItemStack[]{Items.AIR_COMPRESSOR});
        this.registerResearch("canisters", 69696972, "Dep\u00f3sito de contaminaci\u00f3n", 6, new ItemStack[]{Items.EMPTY_CANISTER, Items.CO2_CANISTER});
        this.registerResearch("filter", 69696973, "Filtrado", 8, new ItemStack[]{Items.FILTER});
        this.registerResearch("mercury", 69696973, "Mercurio", 12, new ItemStack[]{Items.CINNABARITE, Items.MERCURY});
        this.registerResearch("climatizador", 69696974, "Climatizaci\u00f3n", 45, new ItemStack[]{Items.CLIMATIZADOR});
        this.registerResearch("sumidero_carbono", 69696975, "Captura de carbono", 50, new ItemStack[]{Items.SUMIDERO_CARBONO});
    }

    private void arrancarEventosClimaticos() {
        if (!((Boolean)this.cfg.getOrSetDefault("eventos-climaticos.enabled", (Object)true)).booleanValue()) {
            return;
        }
        EnumMap<EventoClimatico, Boolean> permitidos = new EnumMap<EventoClimatico, Boolean>(EventoClimatico.class);
        for (EventoClimatico e : EventoClimatico.values()) {
            permitidos.put(e, (Boolean)this.cfg.getOrSetDefault("eventos-climaticos.tipos." + e.getClaveConfig(), (Object)true));
        }
        gestorEventos = new GestorEventos((Integer)this.cfg.getOrSetDefault("eventos-climaticos.intervalo-sorteo-segundos", (Object)600), (Double)this.cfg.getOrSetDefault("eventos-climaticos.probabilidad", (Object)0.35), (Integer)this.cfg.getOrSetDefault("eventos-climaticos.duracion-minima-segundos", (Object)240), (Integer)this.cfg.getOrSetDefault("eventos-climaticos.duracion-maxima-segundos", (Object)600), permitidos, (Boolean)this.cfg.getOrSetDefault("eventos-climaticos.anunciar", (Object)true));
        gestorEventos.arrancar(1);
    }

    private void scheduleTasks() {
        if (this.cfg.getBoolean("mechanics.FOREST_FIRES.enabled")) {
            new FireTask((Double)this.cfg.getOrSetDefault("mechanics.FOREST_FIRES.min-temperature-in-celsius", (Object)40.0), (Double)this.cfg.getOrSetDefault("mechanics.FOREST_FIRES.chance", (Object)0.3), (Integer)this.cfg.getOrSetDefault("mechanics.FOREST_FIRES.fire-per-second", (Object)10)).scheduleRepeating(0L, 20L);
        }
        if (this.cfg.getBoolean("mechanics.ICE_MELTING.enabled")) {
            new MeltTask((Double)this.cfg.getOrSetDefault("mechanics.ICE_MELTING.min-temperature-in-celsius", (Object)2.0), (Double)this.cfg.getOrSetDefault("mechanics.ICE_MELTING.chance", (Object)0.5), (Integer)this.cfg.getOrSetDefault("mechanics.ICE_MELTING.melt-per-second", (Object)10)).scheduleRepeating(0L, 20L);
        }
        if (this.cfg.getBoolean("mechanics.SLOWNESS.enabled")) {
            new SlownessTask((Double)this.cfg.getOrSetDefault("mechanics.SLOWNESS.chance", (Object)0.8)).scheduleRepeating(0L, 200L);
        }
        if (this.cfg.getBoolean("mechanics.BURN.enabled")) {
            new BurnTask((Double)this.cfg.getOrSetDefault("mechanics.BURN.chance", (Object)0.8)).scheduleRepeating(0L, 200L);
        }
        this.arrancarEventosClimaticos();
        this.temperatureManager.runCalculationTask(0L, 100L);
    }

    private void registerResearch(String key, int id, String name, int defaultCost, ItemStack ... items) {
        Research research = new Research(new NamespacedKey((Plugin)this, key), id, name, defaultCost);
        for (ItemStack item : items) {
            SlimefunItem sfItem = SlimefunItem.getByItem((ItemStack)item);
            if (sfItem == null) continue;
            research.addItems(new SlimefunItem[]{sfItem});
        }
        research.register();
    }

    public static Registry getRegistry() {
        return registry;
    }

    public static TemperatureManager getTemperatureManager() {
        return GlobalWarmingPlugin.instance.temperatureManager;
    }

    public static Silenciados getSilenciados() {
        return silenciados;
    }

    public static GestorEventos getGestorEventos() {
        return gestorEventos;
    }

    public static GlobalWarmingPlugin getInstance() {
        return instance;
    }

    public static GlobalWarmingCommand getCommand() {
        return GlobalWarmingPlugin.instance.command;
    }

    public String getBugTrackerURL() {
        return "https://github.com/SlimefunNewHorizons/GlobalWarming/issues";
    }

    public JavaPlugin getJavaPlugin() {
        return this;
    }

    public static Config getCfg() {
        return GlobalWarmingPlugin.instance.cfg;
    }

    public static Config getMessagesConfig() {
        return GlobalWarmingPlugin.instance.messages;
    }

    static {
        registry = new Registry();
    }
}

