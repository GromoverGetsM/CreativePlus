package ru.rstudios.creativeplus.creative.menus.main;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.rstudios.creativeplus.creative.menus.CreativeSystemMenu;
import ru.rstudios.creativeplus.utils.InventoryUtil;


import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class WorldsMenu extends CreativeSystemMenu implements Listener {

    private String name;
    private int rows;
    private HashMap<Integer, ItemStack> items;

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

        registerEvents();
    }


    @Override
    public @NotNull Inventory getInventory() {
        File inventory = new File(plugin.getDataFolder() + File.separator + "menus" + File.separator + this.getClass().getSimpleName());
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
            i = InventoryUtil.getInventory(YamlConfiguration.loadConfiguration(inventory));
            List<Integer> plotsAllowed = Arrays.asList();
        }

        return i;
    }

    @Override
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory().getHolder() instanceof WorldsMenu);
    }

    @Override
    public void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
