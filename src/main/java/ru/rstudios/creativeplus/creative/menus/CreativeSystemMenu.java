package ru.rstudios.creativeplus.creative.menus;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.rstudios.creativeplus.utils.InventoryUtil;

import java.io.File;
import java.util.HashMap;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public abstract class CreativeSystemMenu implements InventoryHolder {

    private String name;
    private int rows;
    private HashMap<Integer, ItemStack> items;

    public CreativeSystemMenu (String name) {
        this(name, 1);
    }

    public CreativeSystemMenu (String name, int rows) {
        this(name, rows, null);
    }

    public CreativeSystemMenu (String name, int rows, HashMap<Integer, ItemStack> items) {
        this.name = name;
        this.rows = Math.min(rows, 6);
        this.items = items;
    }

    public String getName() {
        return this.name;
    }

    public int getRows() {
        return this.rows;
    }

    public HashMap<Integer, ItemStack> getItems() {
        return this.items;
    }

    public void setName (String name) {
        this.name = name;
    }

    public void setRows (int rows) {
        this.rows = Math.min(rows, 6);
    }

    public void setItems (HashMap<Integer, ItemStack> items) {
        this.items = items;
    }

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
            i = InventoryUtil.getInventory(YamlConfiguration.loadConfiguration(inventory), this);
        }

        return i;
    }
}
