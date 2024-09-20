package ru.rstudios.creativeplus.creative.coding.actions;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;

import java.util.List;

public abstract class ActionIf extends ArrayAction {

    List<Action> condActions;
    private boolean inverted;
    public ActionIf(Starter starter, String name, Inventory inventory, List<Action> actions) {
        super(starter, name, inventory, actions);
        this.condActions = actions;
    }

    public boolean checkCondition(GameEvent event, List<Entity> selection) {
        return this.inverted != this.conditionExpression(event, selection);
    }

    public abstract boolean conditionExpression(GameEvent event, List<Entity> selection);
}
