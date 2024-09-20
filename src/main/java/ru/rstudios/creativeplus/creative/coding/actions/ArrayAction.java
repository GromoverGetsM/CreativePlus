package ru.rstudios.creativeplus.creative.coding.actions;

import org.bukkit.inventory.Inventory;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;

import java.util.List;

public abstract class ArrayAction extends Action {

    private List<Action> actions;

    public ArrayAction(Starter starter, String name, Inventory inventory, List<Action> actions) {
        super(starter, name, inventory);
        this.actions = actions;
    }

    public List<Action> getActions() {
        return this.actions;
    }
}
