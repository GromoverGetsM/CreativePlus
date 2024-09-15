package ru.rstudios.creativeplus.creative.coding.starters.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.events.GamePlayerEvent;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.creative.plots.Plot;

import java.util.ArrayList;
import java.util.List;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class PlayerJoinStarter extends Starter implements Listener {

    private List<Action> actions = new ArrayList<>();
    private List<Entity> selection = new ArrayList<>();

    public PlayerJoinStarter() { this("Игрок зашёл"); }

    public PlayerJoinStarter(String name) {
        this(name, new ArrayList<>());
    }

    public PlayerJoinStarter (String name, List<Action> actions) {
        super(name, actions);
        registerEvents();
    }

    @Override
    public List<Entity> getSelection() {
        return this.selection;
    }

    @Override
    public void setSelection(List<Entity> selection) {
        this.selection = selection;
    }

    @EventHandler
    public void on (PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        Plot plot = Plot.getByPlayer(player);

        if (player.getWorld() != plot.getLinkedDevPlot().getWorld()) {
            plot.getHandler().sendStarter(new Event(player, plot, event));
        }

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
    public void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void executeActions() {
        for (Action action : actions) {
            action.execute();
        }
    }

    public static class Event extends GamePlayerEvent {

        public Event(Player player, Plot plot, org.bukkit.event.Event event) {
            super(player, plot, event);
        }
    }
}
