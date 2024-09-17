package ru.rstudios.creativeplus.creative.menus.main;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.rstudios.creativeplus.creative.menus.CreativeSystemMenu;
import ru.rstudios.creativeplus.creative.plots.Plot;
import ru.rstudios.creativeplus.utils.InventoryUtil;


import java.io.File;
import java.util.*;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class WorldsMenu extends CreativeSystemMenu {

    private final String name;
    private final int rows;
    private final HashMap<Integer, ItemStack> items;

    public WorldsMenu (String name) {
        this(name, 1);
    }

    public WorldsMenu (String name, int rows) {
        this(name, rows, null);
    }

    public WorldsMenu (String name, int rows, HashMap<Integer, ItemStack> items) {
        super(name, rows, items);
        this.name = name;
        this.rows = rows;
        this.items = items;
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
            List<Integer> plotsAllowed = Arrays.asList(20, 21, 22, 23, 24, 29, 30, 31, 32, 33);

            Collection<Plot> p = Plot.plots.values();
            List<Plot> plots = new ArrayList<>(p);

            plots.sort(Comparator.comparingInt(Plot::getPlotOnline));

            for (int j = 0; j < plotsAllowed.size(); j++) {
                if (plots.isEmpty()) break;
                if (j < plots.size()) i.setItem(plotsAllowed.get(j), plots.get(j).getIcon());
            }
        }

        return i;
    }
}
