package ru.rstudios.creativeplus.creative.coding.starters.player;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.events.BlockEvent;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;
import ru.rstudios.creativeplus.creative.coding.events.GamePlayerEvent;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.creative.plots.Plot;

import java.util.ArrayList;
import java.util.List;

public class PlayerPlaceBlockStarter extends Starter {

    private List<Action> actions = new ArrayList<>();
    private List<Entity> selection = new ArrayList<>();

    public PlayerPlaceBlockStarter() { this("Поставил блок"); }

    public PlayerPlaceBlockStarter(String name) {
        this(name, new ArrayList<>());
    }

    public PlayerPlaceBlockStarter (String name, List<Action> actions) {
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
        return "Поставил блок";
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
            action.execute(event);
        }
    }

    public static class Event extends GamePlayerEvent implements BlockEvent, Cancellable {


        public Event(Player player, Plot plot, org.bukkit.event.Event event) {
            super(player, plot, event);
        }

        @Override
        public boolean isCancelled() {
            return ((BlockPlaceEvent) this.getHandleEvent()).isCancelled();
        }

        @Override
        public void setCancelled(boolean b) {
            ((BlockPlaceEvent) this.getHandleEvent()).setCancelled(b);
        }

        @Override
        public Block getEventBlock() {
            return ((BlockPlaceEvent) this.getHandleEvent()).getBlock();
        }

        @Override
        public BlockFace getBlockFace() {
            return ((BlockPlaceEvent) this.getHandleEvent()).getBlock().getFace(this.getEventBlock());
        }
    }

}
