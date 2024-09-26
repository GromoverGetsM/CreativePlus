package ru.rstudios.creativeplus.creative.coding.starters.player;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.events.BlockEvent;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;
import ru.rstudios.creativeplus.creative.coding.events.GamePlayerEvent;
import ru.rstudios.creativeplus.creative.coding.events.ItemEvent;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.creative.plots.Plot;

import java.util.ArrayList;
import java.util.List;

public class PlayerRightClickStarter extends Starter {

    private List<Action> actions = new ArrayList<>();
    private List<Entity> selection = new ArrayList<>();

    public PlayerRightClickStarter() { this("Правый клик"); }

    public PlayerRightClickStarter(String name) {
        this(name, new ArrayList<>());
    }

    public PlayerRightClickStarter (String name, List<Action> actions) {
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
        return "Правый клик";
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

    public static class Event extends GamePlayerEvent implements BlockEvent, ItemEvent, Cancellable {

        public Event(Player player, Plot plot, org.bukkit.event.Event event) {
            super(player, plot, event);
        }

        @Override
        public boolean isCancelled() {
            return ((PlayerInteractEvent) this.getHandleEvent()).isCancelled();
        }

        @Override
        public void setCancelled(boolean b) {
            ((PlayerInteractEvent) this.getHandleEvent()).setCancelled(b);
        }

        @Override
        public Block getEventBlock() {
            return ((PlayerInteractEvent) this.getHandleEvent()).getClickedBlock();
        }

        @Override
        public BlockFace getBlockFace() {
            return ((PlayerInteractEvent) this.getHandleEvent()).getBlockFace();
        }

        @Override
        public ItemStack getItem() {
            return ((PlayerInteractEvent) this.getHandleEvent()).getItem();
        }

        @Override
        public void setItem(ItemStack item) {
            return;
        }
    }

}
