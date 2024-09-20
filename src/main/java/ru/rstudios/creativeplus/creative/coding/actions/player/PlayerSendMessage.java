package ru.rstudios.creativeplus.creative.coding.actions.player;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.actions.ActionType;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.utils.CodingHandleUtils;

import java.util.List;

public class PlayerSendMessage extends Action {

    private final Inventory inventory;
    private Starter starter;

    public PlayerSendMessage(Starter starter, String name, Inventory inventory) {
        super(starter, name, inventory);
        this.inventory = inventory;
        this.starter = starter;
    }

    @Override
    public ItemStack getIcon() {
        return null;
    }

    @Override
    public String getName() {
        return "Отправить сообщение";
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public ActionType getType() {
        return ActionType.SEND_MESSAGE;
    }

    @Override
    public void execute (GameEvent event) {
        List<Entity> selection = starter.getSelection();

        for (Entity entity : selection) {
            String message = parseMessage(this.inventory, entity, event);
            entity.sendMessage(message);
        }
    }

    private String parseMessage (Inventory inventory, Entity entity, GameEvent gameEvent) {
        StringBuilder builder = new StringBuilder();

        for (int i = 9; i < 35; i++) {

            if (inventory.getItem(i) != null) {
                builder.append(CodingHandleUtils.parseItem(inventory.getItem(i), gameEvent, entity));
            }
        }

        return builder.toString();
    }
}
