package ru.rstudios.creativeplus.creative.plots;

import org.bukkit.World;

import java.io.File;

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
        devPlotName = linked.getPlotName().replace("_CraftPlot", "_dev");

    }

}
