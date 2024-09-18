package ru.rstudios.creativeplus.creative.coding.starters.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;
import ru.rstudios.creativeplus.creative.coding.events.GamePlayerEvent;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.creative.plots.Plot;

import java.util.ArrayList;
import java.util.List;

public class PlayerJoinStarter extends Starter {

    private List<Action> actions = new ArrayList<>();
    private List<Entity> selection = new ArrayList<>();

    public PlayerJoinStarter() { this("Игрок зашёл"); }

    public PlayerJoinStarter(String name) {
        this(name, new ArrayList<>());
    }

    public PlayerJoinStarter (String name, List<Action> actions) {
        super(name, actions);
    }

    @Override
    public List<Entity> getSelection() {
        return this.selection;
    }

    @Override
    public void setSelection(List<Entity> selection) {
        this.selection = selection;
    }

    @Override
    public List<Action> getActions() {
        return actions;
    }

    @Override
    public String getName() {
        return "Вход";
    }

    @Override
    public ItemStack getIcon() {
        return null;
    }

    @Override
    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    @Override
    public void executeActions (GameEvent event) {
        for (Action action : actions) {
            action.setEvent(event);
            action.execute();
        }
    }

    public static class Event extends GamePlayerEvent {

        public Event(Player player, Plot plot, org.bukkit.event.Event event) {
            super(player, plot, event);
        }
    }
}
