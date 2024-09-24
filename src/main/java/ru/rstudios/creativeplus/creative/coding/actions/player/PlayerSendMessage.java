package ru.rstudios.creativeplus.creative.coding.actions.player;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.actions.ActionType;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.creative.menus.coding.SwitchItem;

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
            String message = parseMessage(entity, event);
            entity.sendMessage(message);
        }
    }

    private String parseMessage (Entity entity, GameEvent gameEvent) {
        StringBuilder builder = new StringBuilder();

        this.initInventorySort();

        List<String> messages = this.getAsTexts(gameEvent, entity);
        SwitchItem item = SwitchItem.getByConfigName("SendMessage#40");
        if (item != null) {
            switch (item.getStateName(item.getCurrentState(this.getNonNullItems()[this.getNonNullItems().length - 1]))) {
                case "Слитно" -> messages.forEach(builder::append);
                case "Разделение пробелом" -> {
                    for (String message : messages) {
                        builder.append(" ").append(message);
                    }
                }
                case "Разделение новой строкой" -> {
                    for (String message : messages) {
                        builder.append('\n').append(message);
                    }
                }
            }
        } else {
            messages.forEach(builder::append);
        }

        return this.replacePlaceholders(builder.toString().trim(), gameEvent, entity);
    }
}
