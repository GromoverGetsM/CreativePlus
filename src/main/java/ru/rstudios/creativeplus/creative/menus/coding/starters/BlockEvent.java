package ru.rstudios.creativeplus.creative.menus.coding.starters;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.rstudios.creativeplus.creative.menus.coding.AbstractCategoryMenu;
import ru.rstudios.creativeplus.creative.menus.coding.AbstractSelectCategoryMenu;
import ru.rstudios.creativeplus.utils.InventoryUtil;

import java.io.File;
import java.util.HashMap;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class BlockEvent extends AbstractSelectCategoryMenu {

    private final String name;
    private final int rows;
    private final HashMap<Integer, ItemStack> items;
    private Player player;

    public BlockEvent (String name) {
        this(name, 1);
    }

    public BlockEvent (String name, int rows) {
        this(name, rows, null);
    }

    public BlockEvent (String name, int rows, HashMap<Integer, ItemStack> items) {
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
        File inventory = new File(plugin.getDataFolder() + File.separator + "menus" + File.separator + "coding" + File.separator + "starters" + File.separator + this.getClass().getSimpleName() + ".yml");
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

    public static class MachineCategory extends AbstractCategoryMenu {

        private final String name;
        private final int rows;
        private final HashMap<Integer, ItemStack> items;
        private Player player;

        public MachineCategory() {
            this("Событие блока");
        }
        public MachineCategory (String name) {
            this(name, 1);
        }

        public MachineCategory (String name, int rows) {
            this(name, rows, null);
        }

        public MachineCategory (String name, int rows, HashMap<Integer, ItemStack> items) {
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
            File inventory = new File(plugin.getDataFolder() + File.separator + "menus" + File.separator + "coding" + File.separator + "starters" + File.separator + "blockevent" + File.separator + this.getClass().getSimpleName() + ".yml");
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

}
