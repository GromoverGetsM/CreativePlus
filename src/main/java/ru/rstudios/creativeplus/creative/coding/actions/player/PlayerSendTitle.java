package ru.rstudios.creativeplus.creative.coding.actions.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.utils.CodingHandleUtils;

import java.util.List;

public class PlayerSendTitle extends Action {

    private final Inventory inventory;
    private Starter starter;

    public PlayerSendTitle (Starter starter, String name, Inventory inventory) {
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
        return "Отправить титл";
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public void execute() {
        List<Entity> selection = starter.getSelection();

        String title = CodingHandleUtils.parseText(this.inventory.getItem(9));
        String subtitle = CodingHandleUtils.parseText(this.inventory.getItem(11));
        int fadein = (int) CodingHandleUtils.parseNumber(this.inventory.getItem(13));
        int duration = (int) CodingHandleUtils.parseNumber(this.inventory.getItem(15));
        int fadeout = (int) CodingHandleUtils.parseNumber(this.inventory.getItem(17));

        for (Entity entity : selection) {
            if (entity instanceof Player player) {
                player.sendTitle(title, subtitle, fadein, duration, fadeout);
            }
        }
    }

}
