package ru.rstudios.creativeplus.creative.coding.actions.player;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;

import java.util.List;

public class PlayerSendMessage extends Action {

    private Inventory inventory;

    public PlayerSendMessage(Starter starter, List<Entity> selection, String name, Inventory inventory) {
        super(starter, selection, name, inventory);
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
        return inventory;
    }

    @Override
    public void execute() {
        for (Entity entity : selection) {
            entity.sendMessage("1");
        }
    }
}
