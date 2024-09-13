package ru.rstudios.creativeplus.creative.plots;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import ru.rstudios.creativeplus.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class DevPlot {

    private Plot linked;
    private File chestsFolder;
    private File jsonCode;
    private String devPlotName;
    private World world;
    private boolean isLoaded;

    public DevPlot (Plot linked) {
        this.linked = linked;
        this.devPlotName = linked.getPlotName().replace("_CraftPlot", "_dev");
        this.isLoaded = !(Bukkit.getWorld(this.devPlotName) == null);

        Plot.linkedPlots.put(linked, this);
        linked.setLinked(this);
    }

    private void create() {
        File template = new File(plugin.getDataFolder() + File.separator + "templates" + File.separator + "dev" + File.separator);
        File dev = new File(Bukkit.getWorldContainer() + File.separator + this.devPlotName);
        FileUtil.copyFilesTo(template, dev);
        if (new File(dev + File.separator + "chests").mkdirs()) this.chestsFolder = new File(dev + File.separator + "chests");
        File jsonCode = new File(dev + File.separator + "code.json");
        try {
            if (jsonCode.createNewFile()) this.jsonCode = jsonCode;
        } catch (IOException e) {
            plugin.getLogger().severe(e.getLocalizedMessage());
        }
    }

    public void load() {
        if (this.exists()) {
            File unloadedWorld = new File(plugin.getDataFolder() + File.separator + "unloadedWorlds" + File.separator + this.devPlotName);

            FileUtil.moveFilesTo(unloadedWorld, Bukkit.getWorldContainer());
        } else {
            create();
        }
        Bukkit.createWorld(new WorldCreator(this.devPlotName));
        this.world = Bukkit.getWorld(this.devPlotName);
        this.isLoaded = true;
    }

    private boolean exists() {
        List<File> files = new LinkedList<>();
        files.addAll(Arrays.stream(Bukkit.getWorldContainer().listFiles()).filter(File::isDirectory).toList());
        files.addAll(Arrays.stream(new File(plugin.getDataFolder() + File.separator + "unloadedWorlds").listFiles()).filter(File::isDirectory).toList());

        boolean found = false;

        if (devPlotName == null || devPlotName.isEmpty()) {
            return false;
        } else {
            for (File file : files) {
                if (file.getName().equalsIgnoreCase(devPlotName)) {
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

}
