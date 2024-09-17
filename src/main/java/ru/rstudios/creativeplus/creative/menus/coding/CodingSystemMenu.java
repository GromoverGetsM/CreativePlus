package ru.rstudios.creativeplus.creative.menus.coding;

import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.menus.CreativeSystemMenu;

import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class CodingSystemMenu extends CreativeSystemMenu {
    public CodingSystemMenu (String name) {
        this(name, 1);
    }

    public CodingSystemMenu (String name, int rows) {
        this(name, rows, new LinkedHashMap<>());
    }

    public CodingSystemMenu (String name, int rows, HashMap<Integer, ItemStack> items) {
        super(name, rows, items);
    }
}
