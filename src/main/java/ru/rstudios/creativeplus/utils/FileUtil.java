package ru.rstudios.creativeplus.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class FileUtil {

    public static FileConfiguration loadConfiguration (String fileName) {
        return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + File.separator + fileName));
    }

    public static FileConfiguration loadConfiguration (File file) {
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void save (FileConfiguration fc, File f) {
        try {
            fc.save(f);
        } catch (IOException e) {
            plugin.getLogger().severe("Error in FileUtil :37 - " + e.getLocalizedMessage());
        }
    }

    public static void moveFilesTo (File fromFolder, File destinationFolder) {
        File[] files = fromFolder.listFiles();

        if (files != null && files.length != 0) {
            for (File f : files) {
                if (f.isDirectory()) {
                    try {
                        org.apache.commons.io.FileUtils.moveDirectory(f, destinationFolder);
                    } catch (IOException e) {
                        plugin.getLogger().severe("Error in FileUtil :50 - " + e.getLocalizedMessage());
                    }
                } else {
                    try {
                        org.apache.commons.io.FileUtils.moveFile(f, destinationFolder);
                    } catch (IOException e) {
                        plugin.getLogger().severe("Error in FileUtil :56 - " + e.getLocalizedMessage());
                    }
                }
            }
        }
    }

    public static void copyFilesTo (File fromFolder, File destinationFolder) {
        File[] files = fromFolder.listFiles();

        if (files != null && files.length != 0) {
            for (File f : files) {
                if (f.isDirectory()) {
                    try {
                        org.apache.commons.io.FileUtils.copyDirectoryToDirectory(f, destinationFolder);
                    } catch (IOException e) {
                        plugin.getLogger().severe("Error in FileUtil :72 - " + e.getLocalizedMessage());
                    }
                } else {
                    try {
                        org.apache.commons.io.FileUtils.copyFileToDirectory(f, destinationFolder);
                    } catch (IOException e) {
                        plugin.getLogger().severe("Error in FileUtil :78 - " + e.getLocalizedMessage());
                    }
                }
            }
        }
    }

    public static void createStarterFolder(String folderName) {
        File folder = new File(plugin.getDataFolder(), folderName);
        if (folder.exists()) {
            if (folder.isFile()) {
                folder.delete();
                folder.mkdir();
            }
        } else {
            folder.mkdir();
        }
    }


    public static void saveResourceFolder(String resourcePath, File destination) throws IOException {
        if (!destination.exists()) {
            destination.mkdirs();
        }

        try {
            File jarFile = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            if (jarFile.isFile()) {
                try (JarFile jar = new JarFile(jarFile)) {
                    Enumeration<JarEntry> entries = jar.entries();

                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();

                        if (entryName.startsWith(resourcePath)) {
                            File entryDestination = new File(destination, entryName.substring(resourcePath.length()));

                            if (entry.isDirectory()) {
                                entryDestination.mkdirs();
                            } else {
                                try (InputStream entryStream = jar.getInputStream(entry)) {
                                    Files.copy(entryStream, entryDestination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                }
                            }
                        }
                    }
                }
            } else {
                throw new IOException("Плагин не запущен из JAR-файла");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Ошибка при загрузке ресурса папки: " + resourcePath);
        }
    }

    public static List<File> getWorldsList (boolean includeUnloaded) {
        List<File> files = new LinkedList<>();
        files.addAll(Arrays.stream(Bukkit.getWorldContainer().listFiles()).filter(File::isDirectory).filter(File -> File.getName().endsWith("CraftPlot")).toList());
        if (includeUnloaded) files.addAll(Arrays.stream(new File(plugin.getDataFolder() + File.separator + "unloadedWorlds").listFiles()).filter(File::isDirectory).filter(File -> File.getName().endsWith("CraftPlot")).toList());

        return files;
    }

}
