package ru.rstudios.creativeplus.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class FileUtil {

    public static FileConfiguration loadConfiguration (String fileName) {
        return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + File.separator + fileName));
    }

    public static FileConfiguration loadConfiguration (File file) {
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void save (File f) {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
        try {
            fc.save(f);
        } catch (IOException e) {
            plugin.getLogger().severe(e.getLocalizedMessage());
        }
    }

}
