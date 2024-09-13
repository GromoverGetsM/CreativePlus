package ru.rstudios.creativeplus.creative.plots;

import com.jeff_media.jefflib.ItemStackSerializer;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import ru.rstudios.creativeplus.creative.tech.GameCategories;
import ru.rstudios.creativeplus.player.PlayerInfo;
import ru.rstudios.creativeplus.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class Plot implements Listener {

    private String plotName;
    private Integer id;
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
    public static @Nullable Plot getById (Integer id) {
        Plot plot1 = null;
        for (Plot plot : plots.values()) {
            if (Objects.equals(plot.getId(), id)) plot1 = plot;
        }
        return plot1;
    }

    public static @Nullable Plot getByWorld (World world) {
        Plot plot1 = null;
        for (Plot plot : plots.values()) {
            if (Objects.equals(plot.getPlotWorld(), world) || Objects.equals(plot.linked.getWorld(), world)) plot1 = plot;
        }

        return plot1;
    }

    public static @Nullable Plot getByPlayer (Player player) {
        return getByWorld(player.getWorld());
    }

    public static String getNextPlotName() {
        FileConfiguration config = FileUtil.loadConfiguration("config.yml");
        return "world_plot_" + (config.getInt("lastWorldId", 0) + 1) + "_CraftPlot";
    }

    public static void loadPlots() {
        List<File> files = FileUtil.getWorldsList(true);

        if (!files.isEmpty()) {
            for (File file : files) {
                FileConfiguration settings = YamlConfiguration.loadConfiguration(new File(file + File.separator + "settings.yml"));
                new Plot(file.getName(), Bukkit.getPlayer(settings.getString("owner", "Unknown")));
            }
        }
    }

    public Plot (@Nullable String plotName, Player owner) {
        registerEvents();
        List<File> files = FileUtil.getWorldsList(true);

        boolean found = false;
        File f = null;

        if (plotName != null || !plotName.isEmpty()) {
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
        try {
            config.save(new File(plugin.getDataFolder() + File.separator + "config.yml"));
        } catch (IOException e) {
            plugin.getLogger().severe("Error in Plot :110 - " + e.getLocalizedMessage());
        }

        String plotName = "world_plot_" + id + "_CraftPlot";
        FileConfiguration fc = FileUtil.loadConfiguration(new File(Bukkit.getWorldContainer() + File.separator + plotName + File.separator + "settings.yml"));

        fc.set("name", plotName);
        fc.set("id", id);
        fc.set("owner", owner.getName());
        fc.set("category", GameCategories.SANDBOX.toString());
        try {
            fc.set("icon", ItemStackSerializer.toBase64(createWorldIcon()));
        } catch (IOException e) {
            plugin.getLogger().severe("Error in Plot :123 - " + e.getLocalizedMessage());
        }
        fc.createSection("allowedDevs");
        fc.createSection("allowedBuilders");

        FileUtil.save(fc, new File(Bukkit.getWorldContainer() + File.separator + plotName + File.separator + "settings.yml"));
        FileUtil.save(config, new File(plugin.getDataFolder() + File.separator + "config.yml"));

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
        this.icon = createWorldIcon();

        PlayerInfo.getPlayerInfo(owner).addPlot(id);
        plots.putIfAbsent(plotName, this);
        load(plotName);
        teleportToPlot(this, owner);
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
        if (!isLoaded) load(plotName);
        teleportToPlot(this, owner);
    }

    private void load (String worldName) {
        List<File> files = FileUtil.getWorldsList(true);

        boolean found = false;
        File folder = null;

        for (File file : files) {
            if (file.getName().equalsIgnoreCase(plotName) && file.isDirectory()) {
                found = true;
                folder = file;
                break;
            }
        }

        World w;

        if (found && folder.isDirectory()) {
            if (folder.getPath().contains(plugin.getDataFolder().getName())) FileUtil.moveFilesTo(folder, Bukkit.getWorldContainer());
            w = Bukkit.createWorld(new WorldCreator(worldName).type(WorldType.FLAT).generateStructures(false));
        } else {
            w = Bukkit.createWorld(new WorldCreator(worldName).type(WorldType.FLAT).generateStructures(false));
        }

        this.plot = w;

        isLoaded = true;
    }

    private void savePlotSettings() {
        try {
            this.plotSettings.save(this.plotSettingsF);
        } catch (IOException e) {
            plugin.getLogger().severe("Error in Plot :212 - " + e.getLocalizedMessage());
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

    public Integer getPlotOnline() {
        return (Bukkit.getWorld(this.plotName) == null ? 0 : Bukkit.getWorld(this.plotName).getPlayers().size()) + (Bukkit.getWorld(this.linked.getDevPlotName()) == null ? 0 : Bukkit.getWorld(this.linked.getDevPlotName()).getPlayers().size());
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
            plugin.getLogger().severe("Error in Plot :339 - " + e.getLocalizedMessage());
        }
        savePlotSettings();
    }

    private ItemStack createWorldIcon() {
        ItemStack i = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName("§fИгра от игрока §e" + this.owner);
        meta.setLore(Arrays.asList("§8§oАвтор: " + this.owner, "§f", "§aИдентификатор: §e" + this.id, "§e» Клик, чтобы зайти"));
        i.setItemMeta(meta);
        return i;
    }

    public static void teleportToPlot (Plot plot, Player player) {
        Location teleport = plot.getPlotWorld().getSpawnLocation();

        player.getInventory().clear();
        player.clearTitle();
        player.clearActivePotionEffects();
        player.closeInventory();
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setGameMode(GameMode.ADVENTURE);

        player.teleport(teleport);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE,100,2);
    }

    public void teleportToDev (Player player) {
        player.getInventory().clear();
        player.clearTitle();
        player.clearActivePotionEffects();
        player.closeInventory();
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setGameMode(GameMode.CREATIVE);

        this.linked.load();

        player.teleport(this.linked.getWorld().getSpawnLocation());
    }

    private void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onWorldLoaded (WorldLoadEvent event) {
        World w = event.getWorld();
        if (w.getName().equalsIgnoreCase(this.plotName)) {
            w.getWorldBorder().setSize(1024);
            w.setGameRule(GameRule.SPAWN_RADIUS, 0);
            w.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            w.setGameRule(GameRule.DISABLE_RAIDS, true);
            w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
            w.setDifficulty(Difficulty.PEACEFUL);
        }
    }
}
