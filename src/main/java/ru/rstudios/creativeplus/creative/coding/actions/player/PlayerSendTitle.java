package ru.rstudios.creativeplus.creative.coding.actions.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.actions.ActionType;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;
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
    public ActionType getType() {
        return ActionType.SEND_TITLE;
    }

    @Override
    public void execute (GameEvent event) {
        List<Entity> selection = starter.getSelection();

        for (Entity entity : selection) {
            if (entity instanceof Player player) {
                String title = (String) CodingHandleUtils.parseItem(this.inventory.getItem(9), event, entity, this.starter);
                String subtitle = (String) CodingHandleUtils.parseItem(this.inventory.getItem(11), event, entity, this.starter);
                int fadein = CodingHandleUtils.parseItem(this.inventory.getItem(13), event, entity, this.starter) instanceof Number ? (int) CodingHandleUtils.parseItem(this.inventory.getItem(13), event, entity, this.starter) : 0;
                int duration = CodingHandleUtils.parseItem(this.inventory.getItem(15), event, entity, this.starter) instanceof Number ? (int) CodingHandleUtils.parseItem(this.inventory.getItem(15), event, entity, this.starter) : 0;
                int fadeout = CodingHandleUtils.parseItem(this.inventory.getItem(17), event, entity, this.starter) instanceof Number ? (int) CodingHandleUtils.parseItem(this.inventory.getItem(17), event, entity, this.starter) : 0;

                player.sendTitle(this.replacePlaceholders(title, event), this.replacePlaceholders(subtitle, event), fadein, duration, fadeout);
            }
        }
    }

}
