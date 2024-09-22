package ru.rstudios.creativeplus.creative.coding.actions.actionvar;

import org.bukkit.event.Cancellable;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.actions.ActionType;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;

public class ActionSetVar extends Action {

    private Starter starter;
    private Inventory inventory;
    public ActionSetVar(Starter starter, String name, Inventory inventory) {
        super(starter, name, inventory);
        this.starter = starter;
        this.inventory = inventory;
    }

    @Override
    public ItemStack getIcon() {
        return null;
    }

    @Override
    public String getName() {
        return "Установить (=)";
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public ActionType getType() {
        return ActionType.CANCEL_EVENT;
    }

    @Override
    public void execute(GameEvent event) {

    }

}
