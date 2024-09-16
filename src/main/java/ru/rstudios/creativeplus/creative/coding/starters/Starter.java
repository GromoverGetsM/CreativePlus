package ru.rstudios.creativeplus.creative.coding.starters;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;

import java.util.ArrayList;
import java.util.List;

public abstract class Starter implements Listener {

    private List<Action> linkedActions;
    private final String name;
    private List<Entity> selection;

    public Starter (String name) {
        this(name, new ArrayList<>());
    }

    public Starter (String name, List<Action> actions) {
        this.name = name;
        this.linkedActions = actions;
    }


    public abstract List<Entity> getSelection();
    public abstract void setSelection(List<Entity> selection);
    public abstract ItemStack getIcon();
    public abstract List<Action> getActions();
    public abstract String getName();
    public abstract void setActions(List<Action> actions);
    public abstract void executeActions();

}
