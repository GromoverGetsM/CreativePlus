package ru.rstudios.creativeplus.creative.coding.actions.gameaction;

import org.bukkit.event.Cancellable;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.actions.ActionType;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;

public class CancelEventAction extends Action {

    private Starter starter;
    private Inventory inventory;
    public CancelEventAction(Starter starter, String name, Inventory inventory) {
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
        return "Отменить событие";
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
        if (event instanceof Cancellable) {
            ((Cancellable) event).setCancelled(true);
        }
    }
}
