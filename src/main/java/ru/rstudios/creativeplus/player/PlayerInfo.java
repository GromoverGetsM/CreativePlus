package ru.rstudios.creativeplus.player;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import ru.rstudios.creativeplus.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class PlayerInfo {

    private String name;
    private List<Integer> plots;
    private int plotLimit;
    private File playerData;
    private FileConfiguration playerDataConfiguration;

    private static Map<Player, PlayerInfo> playerInfoMap = new HashMap<>();

    public static PlayerInfo getPlayerInfo (Player player) {
        if (!playerInfoMap.containsKey(player)) {
            new PlayerInfo(player.getName());
        }
        return playerInfoMap.get(player);
    }

    public static void removePlayer (Player player) {
        PlayerInfo info = PlayerInfo.getPlayerInfo(player);
        if (info != null) {
            info.saveConfiguration();
        }

        playerInfoMap.remove(player);
    }

    public PlayerInfo (String name) {
        File f = new File(plugin.getDataFolder() + File.separator + "players" + File.separator + name + ".yml");
        this.playerData = f;
        this.playerDataConfiguration = YamlConfiguration.loadConfiguration(f);

        if (!f.exists() || f.length() == 0) {
            create(name, f);
        } else {
            init(name, f);
        }
    }

    private void create (String name, File file) {
        FileConfiguration fc = FileUtil.loadConfiguration(file);
        playerDataConfiguration.set("name", name);
        playerDataConfiguration.set("plots", new ArrayList<>());
        playerDataConfiguration.set("plotLimit", 3);
        FileUtil.save(fc, file);

        this.name = name;
        this.plots = new LinkedList<>();
        this.plotLimit = 3;
        if (Bukkit.getPlayer(name) != null && Bukkit.getPlayer(name).isOnline()) {
            playerInfoMap.putIfAbsent(Bukkit.getPlayer(name), this);
        }
    }

    private void init (String name, File file) {
        FileConfiguration fc = FileUtil.loadConfiguration(file);
        List<Integer> plots = fc.getIntegerList("plots");

        this.name = name;
        this.plots = plots;
        this.plotLimit = fc.getInt("plotLimit");
        if (Bukkit.getPlayer(name) != null && Bukkit.getPlayer(name).isOnline()) {
            playerInfoMap.putIfAbsent(Bukkit.getPlayer(name), this);
        }
    }

    private void saveConfiguration() {
        FileUtil.save(playerDataConfiguration, playerData);
    }

    private FileConfiguration getConfiguration() {
        return YamlConfiguration.loadConfiguration(playerData);
    }

    public String getName() {
        return this.name;
    }

    public List<Integer> getPlots() {
        return this.plots;
    }

    public int getPlotLimit() {
        return this.plotLimit;
    }

    public void setName (String name) {
        this.name = name;
        getConfiguration().set("name", name);
        saveConfiguration();
    }

    public void setPlots (List<Integer> plots) {
        this.plots = plots;
        getConfiguration().set("plots", plots);
        saveConfiguration();
    }

    public void addPlot (int plot) {
        this.plots.add(plot);
        getConfiguration().set("plots", this.plots);
        saveConfiguration();
    }

    public void removePlot (int plot) {
        this.plots.remove(plot);
        getConfiguration().set("plots", plots);
        saveConfiguration();
    }

    public void setPlotLimit (int plotLimit) {
        this.plotLimit = plotLimit;
        getConfiguration().set("plotLimit", plotLimit);
        saveConfiguration();
    }


}
