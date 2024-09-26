package ru.rstudios.creativeplus.creative.coding.actions;

import org.bukkit.GameEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;

public abstract class ActionSelect extends Action {

    private ActionIf condition;

    public ActionSelect(Starter starter, String name, Inventory inventory) {
        super(starter, name, inventory);
    }

    public ActionIf getCondition() {
        return this.condition;
    }

    public void setCondition (ActionIf condition) {
        this.condition = condition;
    }

    public abstract ItemStack getIcon();
    public abstract String getName();
    public abstract Inventory getInventory();
    public abstract ActionType getType();
    public abstract void execute (GameEvent event);

}
