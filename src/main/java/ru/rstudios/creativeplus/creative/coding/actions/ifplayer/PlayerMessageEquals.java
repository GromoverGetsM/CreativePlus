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

public class PlayerMessageEquals extends ActionIf {

    private final Inventory inventory;
    private Starter starter;
    private List<Action> condActions;
    private boolean inverted = false;

    public PlayerMessageEquals(Starter starter, String name, Inventory inventory, List<Action> actions) {
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
        return "Сообщение равно";
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
                player.sendMessage("§bCreative+ §8» §fВызвано событие с несовместимым условием 'Сообщение равно'");
            }
            return false;
        }

        for (int i = 9; i < 44; i++) {
            ItemStack item = this.inventory.getItem(i);

            if (item != null) {
                String name = CodingHandleUtils.parseItem(item, event, selection.get(new Random().nextInt(0, selection.size())), this.starter) == null ? "" : CodingHandleUtils.parseItem(item, event, selection.get(new Random().nextInt(0, selection.size())), this.starter).toString();
                name = this.replacePlaceholders(name, event);

                if (((ChatEvent) event).getMessage().equalsIgnoreCase(name)) return true;
            }
        }
        return false;
    }

}
