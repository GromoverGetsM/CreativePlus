package ru.rstudios.creativeplus.creative.coding.starters.block;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.events.*;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.creative.plots.Plot;

import java.util.ArrayList;
import java.util.List;

public class BlockDispenseStarter extends Starter {

    private List<Action> actions = new ArrayList<>();
    private List<Entity> selection = new ArrayList<>();

    public BlockDispenseStarter() { this("Блок выдал предмет"); }

    public BlockDispenseStarter(String name) {
        this(name, new ArrayList<>());
    }

    public BlockDispenseStarter (String name, List<Action> actions) {
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
        return "Блок выдал предмет";
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
    public void executeActions(GameEvent event) {
        for (Action action : actions) {
            action.execute(event);
        }
    }

    public static class Event extends GameBlockEvent implements BlockEvent, ItemEvent, Cancellable {


        public Event(Block block, Plot plot, org.bukkit.event.Event event) {
            super(block, plot, event);
        }

        @Override
        public boolean isCancelled() {
            return ((BlockDispenseEvent) this.getHandleEvent()).isCancelled();
        }

        @Override
        public void setCancelled(boolean b) {
            ((BlockDispenseEvent) this.getHandleEvent()).setCancelled(b);
        }

        @Override
        public Block getEventBlock() {
            return ((BlockDispenseEvent) this.getHandleEvent()).getBlock();
        }

        @Override
        public BlockFace getBlockFace() {
            return ((BlockDispenseEvent) this.getHandleEvent()).getBlock().getFace(this.getBlock());
        }

        @Override
        public ItemStack getItem() {
            return ((BlockDispenseEvent) this.getHandleEvent()).getItem();
        }

        @Override
        public void setItem(ItemStack item) {
            ((BlockDispenseEvent) this.getHandleEvent()).setItem(item);
        }

        @Override
        public Plot getPlot() {
            return Plot.getByWorld(this.getBlock().getWorld());
        }

        @Override
        public Entity getDefaultEntity() {
            return null;
        }
    }

}
