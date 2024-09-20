package ru.rstudios.creativeplus.creative.coding.actions;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;

import java.util.List;

public abstract class Action {

    private final String name;
    private final Inventory inventory;
    private final Starter starter;
    protected List<Entity> selection;

    public Action (Starter starter, String name, Inventory inventory) {
        this.name = name;
        this.inventory = inventory;
        this.starter = starter;
    }

    public abstract ItemStack getIcon();
    public abstract String getName();
    public abstract Inventory getInventory();
    public abstract ActionType getType();

    public abstract void execute (GameEvent event);

}
