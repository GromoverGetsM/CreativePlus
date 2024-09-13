package ru.rstudios.creativeplus.creative.plots;

import com.jeff_media.jefflib.ItemStackSerializer;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.rstudios.creativeplus.creative.tech.GameCategories;
import ru.rstudios.creativeplus.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class Plot {

    private String plotName;
    private @NotNull Integer id;
    private String customId;
    private String owner;
    private ItemStack icon;
    private GameCategories category;
    private List<String> allowedDevs;
    private List<String> allowedBuilders;
    private DevPlot linked;
    private FileConfiguration plotSettings;
    private File plotSettingsF;
    private World plot;
    private boolean isLoaded = false;

    public static Map<String, Plot> plots = new HashMap<>();
    public static Map<Plot, DevPlot> linkedPlots = new HashMap<>();

    public static @Nullable Plot getPlot (String plotName) {
        return plots.get(plotName);
    }

    public static @Nullable DevPlot getLinkedDevPlot (String plotName) {
        return linkedPlots.get(plots.get(plotName));
    }

    public static @Nullable DevPlot getLinkedDevPlot (Plot linked) {
        return linkedPlots.get(linked);
    }

    public Plot (@Nullable String plotName, Player owner) {
        List<File> files = new LinkedList<>();
        files.addAll(Arrays.stream(Bukkit.getWorldContainer().listFiles()).filter(File::isDirectory).toList());
        files.addAll(Arrays.stream(new File(plugin.getDataFolder() + File.separator + "unloadedWorlds").listFiles()).filter(File::isDirectory).toList());

        boolean found = false;
        File f = null;

        if (plotName == null || plotName.isEmpty()) {

        } else {
            for (File file : files) {
                if (file.getName().equalsIgnoreCase(plotName)) {
                    f = new File(file + File.separator + "settings.yml");
                    found = true;
                    break;
                }
            }

            if (found) {
                init(plotName, f, owner);
            } else {
                create(owner);
            }
        }
    }

    private void create (Player owner) {
        FileConfiguration config = FileUtil.loadConfiguration("config.yml");

        int id = config.getInt("lastWorldId", 0) + 1;
        config.set("lastWorldId", id);

        String plotName = "world_plot_" + id + "_CraftPlot";
        FileConfiguration fc = FileUtil.loadConfiguration(new File(Bukkit.getWorldContainer() + File.separator + plotName + File.separator + "settings.yml"));

        fc.set("name", plotName);
        fc.set("id", id);
        fc.set("owner", owner);
        fc.set("category", GameCategories.SANDBOX);
        try {
            fc.set("icon", ItemStackSerializer.toBase64(new ItemStack(Material.GRASS_BLOCK)));
        } catch (IOException e) {
            plugin.getLogger().severe(e.getLocalizedMessage());
        }
        fc.createSection("allowedDevs");
        fc.createSection("allowedBuilders");

        FileUtil.save(new File(plugin.getDataFolder() + File.separator + "unloadedWorlds" + File.separator + plotName + File.separator + "settings.yml"));
        FileUtil.save(new File(plugin.getDataFolder() + File.separator + "config.yml"));

        this.plotName = plotName;
        this.id = id;
        this.customId = null;
        this.owner = owner.getName();
        this.category = GameCategories.SANDBOX;
        this.allowedDevs = new LinkedList<>();
        this.allowedBuilders = new LinkedList<>();
        this.plotSettings = fc;
        this.plotSettingsF = new File(Bukkit.getWorldContainer() + File.separator + plotName + File.separator + "settings.yml");
        this.linked = new DevPlot(this);
        this.icon = new ItemStack(Material.GRASS_BLOCK);

        plots.putIfAbsent(plotName, this);
        load(plotName);
    }

    private void init (String plotName, File f, Player owner) {
        FileConfiguration fc = FileUtil.loadConfiguration(f);

        this.plotName = plotName;
        this.id = fc.getInt("id", 0);
        this.customId = fc.getString("customId", null);
        this.owner = owner.getName();
        this.category = GameCategories.valueOf(fc.getString("category", null));
        this.allowedDevs = fc.getStringList("allowedDevs").isEmpty() ? new LinkedList<>() : fc.getStringList("allowedDevs");
        this.allowedBuilders = fc.getStringList("allowedBuilders").isEmpty() ? new LinkedList<>() : fc.getStringList("allowedBuilders");
        this.icon = ItemStackSerializer.fromBase64(fc.getString("icon"));
        this.plotSettings = fc;
        this.plotSettingsF = f;
        this.linked = new DevPlot(this);

        plots.putIfAbsent(plotName, this);
        load(plotName);
    }

    private void load (String worldName) {
        List<File> files = new LinkedList<>();
        files.addAll(Arrays.stream(Bukkit.getWorldContainer().listFiles()).filter(File::isDirectory).toList());
        files.addAll(Arrays.stream(new File(plugin.getDataFolder() + File.separator + "unloadedWorlds").listFiles()).filter(File::isDirectory).toList());

        boolean found = false;
        File folder = null;

        for (File file : files) {
            if (file.getName().equalsIgnoreCase(plotName) && file.isDirectory()) {
                found = true;
                folder = file;
                break;
            }
        }

        if (found && folder.isDirectory()) {
            if (folder.getPath().contains(plugin.getDataFolder().getName())) FileUtil.moveFilesTo(folder, Bukkit.getWorldContainer());
            this.plot = Bukkit.createWorld(new WorldCreator(worldName));
        } else {
            this.plot = Bukkit.createWorld(new WorldCreator(worldName).type(WorldType.FLAT));
            this.plot.getWorldBorder().setSize(1024);
            this.plot.setGameRule(GameRule.SPAWN_RADIUS, 0);
            this.plot.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            this.plot.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            this.plot.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            this.plot.setGameRule(GameRule.DISABLE_RAIDS, true);
            this.plot.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        }

        isLoaded = true;
    }

    private void savePlotSettings() {
        try {
            this.plotSettings.save(this.plotSettingsF);
        } catch (IOException e) {
            plugin.getLogger().severe(e.getLocalizedMessage());
        }
    }

    public String getPlotName() {
        return this.plotName;
    }

    public String getOwner() {
        return plotSettings.getString("owner", this.owner.isEmpty() ? "Unknown" : this.owner);
    }

    public String getCustomId() {
        return plotSettings.getString("customId", this.customId.isEmpty() ? "Plot has no custom ids" : this.customId);
    }

    public Integer getId() {
        return this.id != plotSettings.getInt("id", 0) ? plotSettings.getInt("id", 0) : this.id;
    }

    public GameCategories getCategory() {
        return this.category != GameCategories.valueOf(plotSettings.getString("category", "SANDBOX")) ? GameCategories.valueOf(plotSettings.getString("category", "SANDBOX")) : this.category;
    }

    public List<String> getAllowedDevs() {
        return plotSettings.getStringList("allowedDevs").isEmpty() ? this.allowedDevs.isEmpty() ? new LinkedList<>() : this.allowedDevs : plotSettings.getStringList("allowedDevs");
    }

    public List<String> getAllowedBuilders() {
        return plotSettings.getStringList("allowedBuilders").isEmpty() ? this.allowedBuilders.isEmpty() ? new LinkedList<>() : this.allowedBuilders : plotSettings.getStringList("allowedBuilders");
    }

    public World getPlotWorld() {
        return this.plot;
    }

    public boolean getPlotLoaded() {
        return this.isLoaded;
    }

    public DevPlot getLinkedDevPlot() {
        return this.linked;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public void setPlotName (String plotName) {
        this.plotName = plotName;
        plotSettings.set("name", plotName);
        savePlotSettings();
    }

    public void setOwner (String ownerName) {
        this.owner = ownerName;
        plotSettings.set("owner", ownerName);
        savePlotSettings();
    }

    public void setCustomId (String customId) {
        this.customId = customId;
        plotSettings.set("customId", customId);
        savePlotSettings();
    }

    public void setId (Integer id) {
        this.id = id;
        plotSettings.set("id", id);
        savePlotSettings();
    }

    public void setCategory (GameCategories category) {
        this.category = category;
        plotSettings.set("category", category);
        savePlotSettings();
    }

    public void setAllowedDevs (List<String> allowedDevs) {
        this.allowedDevs = allowedDevs;
        plotSettings.set("allowedDevs", allowedDevs);
        savePlotSettings();
    }

    public void addAllowedDev (String playerName) {
        this.allowedDevs.add(playerName);
        plotSettings.set("allowedDevs", this.allowedDevs);
        savePlotSettings();
    }

    public void removeAllowedDev (String playerName) {
        this.allowedDevs.remove(playerName);
        plotSettings.set("allowedDevs", this.allowedDevs);
        savePlotSettings();
    }

    public void setAllowedBuilders (List<String> allowedBuilders) {
        this.allowedBuilders = allowedBuilders;
        plotSettings.set("allowedDevs", allowedBuilders);
        savePlotSettings();
    }

    public void addAllowedBuilder (String playerName) {
        this.allowedBuilders.add(playerName);
        plotSettings.set("allowedDevs", this.allowedBuilders);
        savePlotSettings();
    }

    public void removeAllowedBuilder (String playerName) {
        this.allowedBuilders.remove(playerName);
        plotSettings.set("allowedDevs", this.allowedBuilders);
        savePlotSettings();
    }

    public void setLinked (DevPlot dev) {
        this.linked = dev;
    }

    public void setIcon (ItemStack icon) {
        this.icon = icon;
        try {
            plotSettings.set("icon", ItemStackSerializer.toBase64(icon));
        } catch (IOException e) {
            plugin.getLogger().severe(e.getLocalizedMessage());
        }
        savePlotSettings();
    }
}
