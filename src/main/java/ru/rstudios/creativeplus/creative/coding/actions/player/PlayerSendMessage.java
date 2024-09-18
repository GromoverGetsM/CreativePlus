package ru.rstudios.creativeplus.creative.coding.actions.player;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.utils.CodingHandleUtils;

import java.util.List;

public class PlayerSendMessage extends Action {

    private final Inventory inventory;
    private Starter starter;
    private GameEvent event;

    public PlayerSendMessage(Starter starter, GameEvent event, String name, Inventory inventory) {
        super(starter, event, name, inventory);
        this.inventory = inventory;
        this.starter = starter;
        this.event = event;
    }

    @Override
    public void setEvent(GameEvent event) {
        this.event = event;
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
    public void execute() {
        List<Entity> selection = starter.getSelection();

        for (Entity entity : selection) {
            String message = parseMessage(this.inventory, entity);
            entity.sendMessage(message);
        }
    }

    private String parseMessage (Inventory inventory, Entity entity) {
        StringBuilder builder = new StringBuilder();

        for (int i = 9; i < 35; i++) {

            if (inventory.getItem(i) != null) {
                switch (inventory.getItem(i).getType()) {
                    case BOOK -> builder.append(CodingHandleUtils.parseText(inventory.getItem(i), ""));
                    case APPLE -> builder.append(CodingHandleUtils.parseGameValue(inventory.getItem(i), event, entity));
                }
            }
        }

        return builder.toString();
    }
}
