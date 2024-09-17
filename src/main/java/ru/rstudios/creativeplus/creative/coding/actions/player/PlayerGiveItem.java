package ru.rstudios.creativeplus.creative.coding.actions.player;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;

import java.util.List;

public class PlayerGiveItem extends Action {

    private final Inventory inventory;
    private Starter starter;

    public PlayerGiveItem (Starter starter, String name, Inventory inventory) {
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
        return "Выдать предметы";
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public void execute() {
        List<Entity> selection = starter.getSelection();

        for (Entity entity : selection) {

            for (int i = 9; i < 35; i++) {
                if (entity instanceof InventoryHolder) {
                    ((InventoryHolder) entity).getInventory().addItem(inventory.getItem(i) == null ? new ItemStack(Material.AIR) : inventory.getItem(i));
                }
            }

        }
    }

}
