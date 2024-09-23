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
    public void execute(GameEvent event) {
        List<Entity> selection = starter.getSelection();
        this.initInventorySort();

        for (Entity entity : selection) {
            if (entity instanceof Player player) {
                String title = this.getTexts().length > 0 ? CodingHandleUtils.parseText(this.getTexts()[0]) : null;
                String subtitle = this.getTexts().length > 1 ? CodingHandleUtils.parseText(this.getTexts()[1]) : null;
                int fadein = this.getNumbers().length > 0 ? (int) CodingHandleUtils.parseNumber(this.getNumbers()[0]) : 0;
                int duration = this.getNumbers().length > 1 ? (int) CodingHandleUtils.parseNumber(this.getNumbers()[1]) : 0;
                int fadeout = this.getNumbers().length > 2 ? (int) CodingHandleUtils.parseNumber(this.getNumbers()[2]) : 0;

                player.sendTitle(
                        this.replacePlaceholders(title, event, entity),
                        this.replacePlaceholders(subtitle, event, entity),
                        fadein, duration, fadeout
                );
            }
        }
    }


}
