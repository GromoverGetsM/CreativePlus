package ru.rstudios.creativeplus;

import org.bukkit.plugin.java.JavaPlugin;
import ru.rstudios.creativeplus.events.Event;
import ru.rstudios.creativeplus.utils.FileUtil;

import java.io.File;
import java.io.IOException;

public final class CreativePlus extends JavaPlugin {

    public static JavaPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        saveResource("messages.yml", false);
        FileUtil.createStarterFolder("templates");
        try {
            FileUtil.saveResourceFolder("dev", new File(getDataFolder() + File.separator + "templates" + File.separator + "dev"));
            FileUtil.saveResourceFolder("menus", new File(getDataFolder() + File.separator + "menus"));
        } catch (IOException e) {
            getLogger().severe(e.getLocalizedMessage());
        }

        getServer().getPluginManager().registerEvents(new Event(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
