package ru.rstudios.creativeplus.creative.menus.main;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.rstudios.creativeplus.creative.menus.CreativeSystemMenu;
import ru.rstudios.creativeplus.creative.plots.Plot;
import ru.rstudios.creativeplus.player.PlayerInfo;
import ru.rstudios.creativeplus.utils.InventoryUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class MyWorlds extends CreativeSystemMenu implements Listener {

    private final String name;
    private final int rows;
    private final HashMap<Integer, ItemStack> items;
    private Player player;

    public MyWorlds (String name) {
        this(name, 1);
    }

    public MyWorlds (String name, int rows) {
        this(name, rows, null);
    }

    public MyWorlds (String name, int rows, HashMap<Integer, ItemStack> items) {
        super(name, rows, items);
        this.name = name;
        this.rows = rows;
        this.items = items;
    }

    public void setPlayer (Player player) {
        this.player = player;
    }


    @Override
    public @NotNull Inventory getInventory() {
        File inventory = new File(plugin.getDataFolder() + File.separator + "menus" + File.separator + this.getClass().getSimpleName() + ".yml");
        Inventory i;

        if (!inventory.exists() || !inventory.isFile() || inventory.length() == 0) {
            i = Bukkit.createInventory(this, this.rows * 9, this.name);

            for (Integer key : this.items.keySet()) {
                if (key <= this.rows * 9) i.setItem(key, this.items.get(key));
                else {
                    plugin.getLogger().warning("Установка предмета невозможна. Слот " + key + " в инвентаре " + this.name + " превышает допустимое значение.");
                    break;
                }
            }
        } else {
            i = InventoryUtil.getInventory(YamlConfiguration.loadConfiguration(inventory), this);
            List<Integer> plotsAllowed = new ArrayList<>();
            IntStream.rangeClosed(10, 16).forEach(plotsAllowed::add);
            IntStream.rangeClosed(19, 25).forEach(plotsAllowed::add);
            IntStream.rangeClosed(28, 34).forEach(plotsAllowed::add);
            IntStream.rangeClosed(37, 43).forEach(plotsAllowed::add);

            PlayerInfo info = PlayerInfo.getPlayerInfo(this.player);
            List<Integer> p = info.getPlots();
            List<Plot> plots = new ArrayList<>();
            p.forEach((id) -> plots.add(Plot.getById(id)));

            for (int j = 0; j < plotsAllowed.size(); j++) {
                if (plots.isEmpty()) break;
                if (j < plots.size()) i.setItem(plotsAllowed.get(j), plots.get(j).getIcon());
            }
        }

        return i;
    }

}
