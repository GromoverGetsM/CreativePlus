package ru.rstudios.creativeplus.creative.menus.coding;

import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.menus.CreativeSystemMenu;

import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class AbstractCategoryMenu extends CreativeSystemMenu {
    public AbstractCategoryMenu(String name) {
        this(name, 1);
    }

    public AbstractCategoryMenu(String name, int rows) {
        this(name, rows, new LinkedHashMap<>());
    }

    public AbstractCategoryMenu(String name, int rows, HashMap<Integer, ItemStack> items) {
        super(name, rows, items);
    }
}
