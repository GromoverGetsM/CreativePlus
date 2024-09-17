package ru.rstudios.creativeplus.creative.menus.coding;

import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.menus.CreativeSystemMenu;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class AbstractSelectCategoryMenu extends CreativeSystemMenu {

    public AbstractSelectCategoryMenu (String name) {
        this(name, 1);
    }
    public AbstractSelectCategoryMenu (String name, int rows) {
        this(name, rows, new LinkedHashMap<>());
    }
    public AbstractSelectCategoryMenu(String name, int rows, HashMap<Integer, ItemStack> items) {
        super(name, rows, items);
    }
}
