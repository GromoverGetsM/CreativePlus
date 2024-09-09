package ru.rstudios.creativeplus.creative.plots;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ru.rstudios.creativeplus.creative.tech.GameCategories;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class Plot {

    private String name;
    private @NotNull Integer id;
    private String customId;
    private String owner;
    private GameCategories category;
    private List<String> allowedDevs;
    private List<String> allowedBuilders;
    private DevPlot linked;

    public Plot (String name) {
        List<File> files = new LinkedList<>();
        files.addAll((Collection<? extends File>) Arrays.stream(Bukkit.getWorldContainer().listFiles()).toList().stream().filter(File::isDirectory));
        files.addAll((Collection<? extends File>) Arrays.stream(new File(plugin.getDataFolder() + File.separator + "unloadedWorlds").listFiles()).toList().stream().filter(File::isDirectory));

        boolean found = false;

        for (File file : files) {
            if (file.getName().equalsIgnoreCase(name)) {
                found = true;
                break;
            }
        }

        if (found) {
            init();
        } else {
            create();
        }

        // Остальное на завтра
    }
}
