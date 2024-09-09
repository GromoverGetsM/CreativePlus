package ru.rstudios.creativeplus;

import org.bukkit.plugin.java.JavaPlugin;
import ru.rstudios.creativeplus.events.Event;

public final class CreativePlus extends JavaPlugin {

    public static JavaPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        saveResource("messages.yml", false);

        getServer().getPluginManager().registerEvents(new Event(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
