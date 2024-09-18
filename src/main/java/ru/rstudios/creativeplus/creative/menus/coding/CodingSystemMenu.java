package ru.rstudios.creativeplus.creative.menus.coding;

import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.menus.CreativeSystemMenu;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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

    public abstract List<Integer> getDisallowedSlots();
}
