package ru.rstudios.creativeplus.creative.plots;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.*;
import ru.rstudios.creativeplus.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class DevPlot {

    public final Plot linked;
    public File chestsFolder;
    public File jsonCode;
    public String devPlotName;
    public World world;
    public boolean isLoaded;

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
            plugin.getLogger().severe("Error in DevPlot :43 - " + e.getLocalizedMessage());
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

    public String getDevPlotName() {
        return this.devPlotName;
    }

    public World getWorld() {
        return this.world;
    }

    public static List<Material> getStarterBlocks() {
        List<Material> list = new ArrayList<>();
        list.add(Material.DIAMOND_BLOCK);
        return list;
    }

    public static List<Material> getActionBlocks() {
        List<Material> list = new ArrayList<>();
        list.add(Material.COBBLESTONE);
        return list;
    }

    public static List<Material> getAllowedBlocks() {
        List<Material> list = new ArrayList<>();
        list.addAll(getStarterBlocks());
        list.addAll(getActionBlocks());
        list.add(Material.CRAFTING_TABLE);
        list.add(Material.ANVIL);
        list.add(Material.CHIPPED_ANVIL);
        list.add(Material.DAMAGED_ANVIL);
        list.add(Material.ENDER_CHEST);
        list.add(Material.PISTON);
        list.add(Material.FURNACE);
        list.add(Material.SHULKER_BOX);
        list.add(Material.BLACK_SHULKER_BOX);
        list.add(Material.BLUE_SHULKER_BOX);
        list.add(Material.LIGHT_BLUE_SHULKER_BOX);
        list.add(Material.CYAN_SHULKER_BOX);
        list.add(Material.GRAY_SHULKER_BOX);
        list.add(Material.BROWN_SHULKER_BOX);
        list.add(Material.GREEN_SHULKER_BOX);
        list.add(Material.LIGHT_GRAY_SHULKER_BOX);
        list.add(Material.LIME_SHULKER_BOX);
        list.add(Material.MAGENTA_SHULKER_BOX);
        list.add(Material.ORANGE_SHULKER_BOX);
        list.add(Material.YELLOW_SHULKER_BOX);
        list.add(Material.WHITE_SHULKER_BOX);
        list.add(Material.RED_SHULKER_BOX);
        list.add(Material.PURPLE_SHULKER_BOX);
        list.add(Material.LOOM);
        list.add(Material.BREWING_STAND);
        list.add(Material.GRINDSTONE);
        return list;
    }

    public boolean inTerritory (Location location) {
        BlockVector3 pos1 = BlockVector3.at(63, -59, 63);
        BlockVector3 pos2 = BlockVector3.at(-64, 255, -64);
        CuboidRegion region = new CuboidRegion(pos1, pos2);

        return region.contains(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

}
