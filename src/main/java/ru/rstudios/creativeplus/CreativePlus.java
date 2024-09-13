package ru.rstudios.creativeplus;

import org.bukkit.plugin.java.JavaPlugin;
import ru.rstudios.creativeplus.commands.CreateInventory;
import ru.rstudios.creativeplus.commands.WorldCommand;
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

        Objects.requireNonNull(getCommand("ic")).setExecutor(new CreateInventory());
        Objects.requireNonNull(getCommand("world")).setExecutor(new WorldCommand());
        Objects.requireNonNull(getCommand("world")).setTabCompleter(new WorldCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
