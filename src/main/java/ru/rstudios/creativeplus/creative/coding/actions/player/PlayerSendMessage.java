package ru.rstudios.creativeplus.creative.coding.actions.player;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.utils.CodingHandleUtils;

import java.util.List;

public class PlayerSendMessage extends Action {

    private final Inventory inventory;

    public PlayerSendMessage(Starter starter, List<Entity> selection, String name, Inventory inventory) {
        super(starter, selection, name, inventory);
        this.inventory = inventory;
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
        String message = parseMessage(this.inventory);

        for (Entity entity : selection) {
            entity.sendMessage(message);
        }
    }

    private String parseMessage (Inventory inventory) {
        StringBuilder builder = new StringBuilder();

        for (int i = 9; i < 35; i++) {

            if (inventory.getItem(i) != null) {
                builder.append(CodingHandleUtils.parseText(inventory.getItem(i), ""));
            }
        }

        return builder.toString();
    }
}
