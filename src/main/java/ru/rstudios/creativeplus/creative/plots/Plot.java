package ru.rstudios.creativeplus.creative.plots;

import com.jeff_media.jefflib.ItemStackSerializer;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import ru.rstudios.creativeplus.creative.tech.GameCategories;
import ru.rstudios.creativeplus.player.PlayerInfo;
import ru.rstudios.creativeplus.utils.FileUtil;
import ru.rstudios.creativeplus.creative.plots.PlotInitializeReason;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class Plot implements Listener {

    private String plotName;
    private Integer id;
    private String customId;
    private String owner;
    private Material icon;
    private String iconName;
    private List<String> iconLore;
    private GameCategories category;
    private List<String> allowedDevs;
    private List<String> allowedBuilders;
    private DevPlot linked;
    private FileConfiguration plotSettings;
    private File plotSettingsF;
    private World plot;
    private PlotInitializeReason reason;
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
        List<File> files = FileUtil.getWorldsList(true, true);

        if (!files.isEmpty()) {
            for (File file : files) {
                FileConfiguration settings = YamlConfiguration.loadConfiguration(new File(file + File.separator + "settings.yml"));
                new Plot(file.getName(), settings.getString("owner", "Unknown"), PlotInitializeReason.SERVER_STARTED);
            }
        }
    }

    public Plot (String plotName, String owner, PlotInitializeReason reason) {
        registerEvents();
        this.reason = reason;
        List<File> files = FileUtil.getWorldsList(true, true);

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
                if (load(plotName)) init(plotName, f, owner);
            } else {
                if (load(plotName)) create(owner);
            }
        }
    }

    private void create (String owner) {
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
        fc.set("owner", owner);
        fc.set("category", GameCategories.SANDBOX.toString());
        fc.set("icon", Material.GRASS_BLOCK.toString());
        fc.set("iconName", "§fИгра от игрока §e" + owner);
        fc.set("iconLore", new ArrayList<>());
        fc.createSection("allowedDevs");
        fc.createSection("allowedBuilders");

        FileUtil.save(fc, new File(Bukkit.getWorldContainer() + File.separator + plotName + File.separator + "settings.yml"));
        FileUtil.save(config, new File(plugin.getDataFolder() + File.separator + "config.yml"));

        this.plotName = plotName;
        this.id = id;
        this.customId = null;
        this.owner = owner;
        this.category = GameCategories.SANDBOX;
        this.allowedDevs = new LinkedList<>();
        this.allowedBuilders = new LinkedList<>();
        this.plotSettings = fc;
        this.plotSettingsF = new File(Bukkit.getWorldContainer() + File.separator + plotName + File.separator + "settings.yml");
        this.linked = new DevPlot(this);
        this.icon = Material.GRASS_BLOCK;
        this.iconName = "§fИгра от игрока §e" + owner;
        this.iconLore = new ArrayList<>();

        PlayerInfo.getPlayerInfo(Bukkit.getPlayer(owner)).addPlot(id);
        plots.putIfAbsent(plotName, this);
        if (Bukkit.getPlayer(owner) != null && Bukkit.getPlayer(owner).isOnline() && this.reason != PlotInitializeReason.SERVER_STARTED) teleportToPlot(this, Bukkit.getPlayer(owner));
        if (this.reason == PlotInitializeReason.SERVER_STARTED) unload(false);
    }

    private void init (String plotName, File f, String owner) {
        FileConfiguration fc = FileUtil.loadConfiguration(f);

        this.plotName = plotName;
        this.id = fc.getInt("id", 0);
        this.customId = fc.getString("customId", null);
        this.owner = owner;
        this.category = GameCategories.valueOf(fc.getString("category", null));
        this.allowedDevs = fc.getStringList("allowedDevs").isEmpty() ? new LinkedList<>() : fc.getStringList("allowedDevs");
        this.allowedBuilders = fc.getStringList("allowedBuilders").isEmpty() ? new LinkedList<>() : fc.getStringList("allowedBuilders");
        this.icon = Material.valueOf(fc.getString("icon"));
        this.iconName = fc.getString("iconName");
        this.iconLore = fc.getStringList("iconLore");
        this.plotSettings = fc;
        this.plotSettingsF = f;
        this.linked = new DevPlot(this);

        plots.putIfAbsent(plotName, this);
        if (Bukkit.getPlayer(owner) != null && Bukkit.getPlayer(owner).isOnline() && this.reason != PlotInitializeReason.SERVER_STARTED) teleportToPlot(this, Bukkit.getPlayer(owner));
        if (this.reason == PlotInitializeReason.SERVER_STARTED) unload(false);
    }

    public boolean load(String worldName) {
        File worldFolder = Arrays.stream(Bukkit.getWorldContainer().listFiles())
                .filter(File::isDirectory)
                .filter(file -> file.getName().endsWith("_CraftPlot"))
                .filter(file -> file.getName().equalsIgnoreCase(worldName))
                .findFirst()
                .orElse(null);

        if (worldFolder == null) {
            worldFolder = Arrays.stream(new File(plugin.getDataFolder() + File.separator + "unloadedWorlds").listFiles())
                    .filter(File::isDirectory)
                    .filter(file -> file.getName().endsWith("_CraftPlot"))
                    .filter(file -> file.getName().equalsIgnoreCase(worldName))
                    .findFirst()
                    .orElse(new File(Bukkit.getWorldContainer() + File.separator + worldName));
        }

        if (!worldFolder.exists()) {
            this.plot = Bukkit.createWorld(new WorldCreator(worldName).type(WorldType.FLAT).generateStructures(false));
        } else if (worldFolder.getParentFile().getName().equalsIgnoreCase("unloadedWorlds")) {
            File world = new File(Bukkit.getWorldContainer() + File.separator + worldName);
            try {
                Files.move(worldFolder.toPath(), world.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                plugin.getLogger().severe(e.getLocalizedMessage());
                return false;
            }
            FileUtil.deleteDirectory(worldFolder);
            this.plot = Bukkit.createWorld(new WorldCreator(worldName));
        } else {
            this.plot = Bukkit.createWorld(new WorldCreator(worldName));
        }

        this.isLoaded = true;
        return true;
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

    public Material getIconMaterial() {
        return this.icon;
    }

    public ItemStack getIcon() {
        ItemStack icon = new ItemStack(this.icon);
        ItemMeta iconMeta = icon.getItemMeta();
        iconMeta.setDisplayName(this.iconName);

        List<String> lore = new ArrayList<>();
        lore.add("§8§oАвтор: " + this.owner);
        lore.add("§f");
        lore.addAll(this.iconLore);
        lore.add("§f");
        lore.add("§aИдентификатор: §e" + this.id);
        lore.add("§e» Клик, чтобы зайти");

        iconMeta.setLore(lore);
        icon.setItemMeta(iconMeta);

        return icon;
    }


    public Integer getPlotOnline() {
        return getPlotOnlineList().isEmpty() ? 0 : getPlotOnlineList().size();
    }

    public List<Player> getPlotOnlineList() {
        List<Player> plotPlayers = new ArrayList<>();

        plotPlayers.addAll(Bukkit.getWorld(this.plotName) == null ? new ArrayList<>() : Bukkit.getWorld(this.plotName).getPlayers());
        plotPlayers.addAll(Bukkit.getWorld(this.linked.getDevPlotName()) == null ? new ArrayList<>() : Bukkit.getWorld(this.linked.getDevPlotName()).getPlayers());
        return plotPlayers;
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

    public void setIcon (Material icon) {
        this.icon = icon;
        plotSettings.set("icon", icon);
        savePlotSettings();
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

    public void unload (boolean bypassPlayers) {
        if (bypassPlayers) {
            for (Player player : getPlotOnlineList()) {
                player.teleport(Bukkit.getWorld("world").getSpawnLocation());

                player.getInventory().clear();
                player.clearTitle();
                player.clearActivePotionEffects();
                player.closeInventory();
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
                player.setGameMode(GameMode.ADVENTURE);

                player.sendMessage("§bCreative+ §8» §fМир в котором ты находился выключился, ты был перемещён на спавн");
            }

            Bukkit.unloadWorld(this.plotName, true);
            this.isLoaded = false;
            Bukkit.unloadWorld(this.linked.getDevPlotName(), true);
            this.linked.isLoaded = false;
        }

    }

    @EventHandler
    public void onWorldUnloaded (WorldUnloadEvent event) {
        World world = event.getWorld();
        if (world.getName().equalsIgnoreCase(this.plotName) || world.getName().equalsIgnoreCase(this.linked.getDevPlotName())) {
            File worldFile = new File(Bukkit.getWorldContainer(), world.getName());
            File destination = new File(plugin.getDataFolder() + File.separator + "unloadedWorlds" + File.separator + world.getName());

            if (!destination.exists() || !destination.isDirectory()) {
                if (!destination.mkdirs()) {
                    plugin.getLogger().severe("Не удалось сохранить мир " + worldFile + ": Папка для отгруженых миров не существует и ее создание невозможно");
                    return;
                }
            }

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                try {
                    Files.move(worldFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    plugin.getLogger().severe(e.getLocalizedMessage());
                }
                FileUtil.deleteDirectory(worldFile);
            }, 60L);
        }
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
            w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            w.setDifficulty(Difficulty.PEACEFUL);
        }
    }
}
