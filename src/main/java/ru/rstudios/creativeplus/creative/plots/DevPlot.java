package ru.rstudios.creativeplus.creative.plots;

import org.bukkit.Bukkit;
import org.bukkit.World;
import ru.rstudios.creativeplus.utils.FileUtil;

import java.io.File;
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

    public DevPlot (Plot linked) {
        this.linked = linked;
    }

    private void create (Plot linked) {
        this.devPlotName = linked.getPlotName().replace("_CraftPlot", "_dev");

        File template = new File(plugin.getDataFolder() + File.separator + "templates" + File.separator + "dev" + File.separator);
        File dev = new File(Bukkit.getWorldContainer() + File.separator + this.devPlotName);
        FileUtil.copyFilesTo(template, dev);

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
