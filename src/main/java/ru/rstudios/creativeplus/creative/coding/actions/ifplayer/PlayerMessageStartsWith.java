package ru.rstudios.creativeplus.creative.coding.actions.ifplayer;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.actions.ActionIf;
import ru.rstudios.creativeplus.creative.coding.actions.ActionType;
import ru.rstudios.creativeplus.creative.coding.events.ChatEvent;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.utils.CodingHandleUtils;

import java.util.List;
import java.util.Random;

public class PlayerMessageStartsWith extends ActionIf {

    private final Inventory inventory;
    private Starter starter;
    private List<Action> condActions;
    private boolean inverted = false;

    public PlayerMessageStartsWith(Starter starter, String name, Inventory inventory, List<Action> actions) {
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
        return "Начинается с";
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
        return this.inverted != this.conditionExpression(event, selection);
    }

    @Override
    public boolean conditionExpression(GameEvent event, List<Entity> selection) {
        if (!(event instanceof ChatEvent)) {
            for (Player player : event.getPlot().getPlotOnlineList()) {
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0F, 1.0F);
                player.sendMessage("§bCreative+ §8» §fВызвано событие с несовместимым условием 'Сообщение начинается с'");
            }
            return false;
        }

        for (ItemStack item : this.getTexts()) {
            if (((ChatEvent) event).getMessage().equalsIgnoreCase(CodingHandleUtils.parseText(item))) return true;
        }
        return false;
    }

}
