package ru.rstudios.creativeplus.creative.coding.actions.ifplayer;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.actions.ActionIf;
import ru.rstudios.creativeplus.creative.coding.actions.ActionType;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.utils.CodingHandleUtils;

import java.util.List;

public class PlayerNameEquals extends ActionIf {

    private final Inventory inventory;
    private Starter starter;
    private List<Action> condActions;
    private boolean inverted = false;

    public PlayerNameEquals(Starter starter, String name, Inventory inventory, List<Action> actions) {
        super(starter, name, inventory, actions);
        this.inventory = inventory;
        this.condActions = actions;
        this.starter = starter;
    }

    @Override
    public ItemStack getIcon() {
        return null;
    }

    @Override
    public String getName() {
        return "Имя равно";
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public ActionType getType() {
        return ActionType.IF_PLAYER_NAME_EQUALS;
    }

    @Override
    public void execute(GameEvent event) {
        if (this.checkCondition(event, starter.getSelection())) {
            for (Action action : condActions) {
                action.execute(event);
            }
        }
    }

    @Override
    public boolean checkCondition(GameEvent event, List<Entity> selection) {
        System.out.println(this.inverted != this.conditionExpression(event, selection));
        return this.inverted != this.conditionExpression(event, selection);
    }

    @Override
    public boolean conditionExpression(GameEvent event, List<Entity> selection) {
        for (Entity entity : selection) {
            for (int i = 9; i < 44; i++) {
                ItemStack item = this.inventory.getItem(i);
                String name = CodingHandleUtils.parseItem(item, event, entity) == null ? "" : CodingHandleUtils.parseItem(item, event, entity).toString();
                if (entity.getName().equalsIgnoreCase(name)) return true;
            }
        }
        return false;
    }
}
