package ru.rstudios.creativeplus.player;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.rstudios.creativeplus.utils.FileUtil;

import java.io.File;
import java.util.*;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class PlayerInfo {

    private String name;
    private List<Integer> plots;

    private static Map<Player, PlayerInfo> playerInfoMap = new HashMap<>();

    public static PlayerInfo getPlayerInfo (Player player) {
        if (!playerInfoMap.containsKey(player)) {
            new PlayerInfo(player.getName());
        }
        return playerInfoMap.get(player);
    }

    public static void removePlayer (Player player) {
        playerInfoMap.remove(player);
    }

    public PlayerInfo (String name) {
        File f = new File(plugin.getDataFolder() + File.separator + "players" + File.separator + name + ".yml")

        if (!f.exists() || f.length() == 0) {
            create(name, f);
        } else {
            init(name, f);
        }
    }

    private void create (String name, File file) {
        FileConfiguration fc = FileUtil.loadConfiguration(file);
        fc.set("name", name);
        fc.set("plots", new ArrayList<>());
        FileUtil.save(file);

        this.name = name;
        this.plots = new LinkedList<>();
        if (Bukkit.getPlayer(name) != null && Bukkit.getPlayer(name).isOnline()) {
            playerInfoMap.putIfAbsent(Bukkit.getPlayer(name), this);
        }
    }

    private void init (String name, File file) {
        FileConfiguration fc = FileUtil.loadConfiguration(file);
        List<Integer> plots = fc.getIntegerList("plots");

        this.name = name;
        this.plots = plots;
        if (Bukkit.getPlayer(name) != null && Bukkit.getPlayer(name).isOnline()) {
            playerInfoMap.putIfAbsent(Bukkit.getPlayer(name), this);
        }
    }


}
