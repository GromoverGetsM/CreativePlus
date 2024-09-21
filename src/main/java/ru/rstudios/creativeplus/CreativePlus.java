package ru.rstudios.creativeplus;

import org.bukkit.plugin.java.JavaPlugin;
import ru.rstudios.creativeplus.commands.CreateInventory;
import ru.rstudios.creativeplus.commands.WorldCommand;
import ru.rstudios.creativeplus.commands.creative.Games;
import ru.rstudios.creativeplus.commands.creative.devCommand;
import ru.rstudios.creativeplus.commands.creative.placeholdersInfoCommand;
import ru.rstudios.creativeplus.commands.creative.playCommand;
import ru.rstudios.creativeplus.creative.coding.GlobalEventListener;
import ru.rstudios.creativeplus.creative.plots.Plot;
import ru.rstudios.creativeplus.events.Event;
import ru.rstudios.creativeplus.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class CreativePlus extends JavaPlugin {

    public static JavaPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();
        saveResource("messages.yml", false);
        FileUtil.createStarterFolder("templates");
        FileUtil.createStarterFolder("unloadedWorlds");
        try {
            FileUtil.saveResourceFolder("dev", new File(getDataFolder() + File.separator + "templates" + File.separator + "dev"));
            FileUtil.saveResourceFolder("menus", new File(getDataFolder() + File.separator + "menus"));
        } catch (IOException e) {
            getLogger().severe("Error in CreativePlus :29 - " + e.getLocalizedMessage());
        }

        getServer().getPluginManager().registerEvents(new Event(), this);
        getServer().getPluginManager().registerEvents(new CreateInventory(), this);
        getServer().getPluginManager().registerEvents(new GlobalEventListener(), this);

        boolean isWEEnabled = getServer().getPluginManager().isPluginEnabled("WorldEdit");
        if (!isWEEnabled) {
            getLogger().severe("WorldEdit 3.3.0 не найден в списке плагинов. Выключаюсь...");
            getServer().getPluginManager().disablePlugin(this);
        }

        Objects.requireNonNull(getCommand("ic")).setExecutor(new CreateInventory());
        Objects.requireNonNull(getCommand("world")).setExecutor(new WorldCommand());
        Objects.requireNonNull(getCommand("world")).setTabCompleter(new WorldCommand());
        Objects.requireNonNull(getCommand("games")).setExecutor(new Games());
        Objects.requireNonNull(getCommand("dev")).setExecutor(new devCommand());
        Objects.requireNonNull(getCommand("play")).setExecutor(new playCommand());
        Objects.requireNonNull(getCommand("placeholders")).setExecutor(new placeholdersInfoCommand());

        Plot.loadPlots();
    }

    @Override
    public void onDisable() {

        for (Plot plot : Plot.plots.values()) {
            if (plot.getPlotLoaded()) {
                plot.getHandler().saveDynamicVariables();
                plot.unload(true);
            }
        }

    }
}
