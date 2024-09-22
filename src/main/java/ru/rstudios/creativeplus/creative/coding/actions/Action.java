package ru.rstudios.creativeplus.creative.coding.actions;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.events.*;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;

import java.util.List;
import java.util.stream.Collectors;

public abstract class Action {

    private final String name;
    private final Inventory inventory;
    private final Starter starter;
    protected List<Entity> selection;

    public Action (Starter starter, String name, Inventory inventory) {
        this.name = name;
        this.inventory = inventory;
        this.starter = starter;
    }

    public String replacePlaceholders (String s, GameEvent event) {
        if (s == null || s.isEmpty()) {
            return null;
        } else {
            s = StringUtils.replace(s, "%selected%", this.starter.getSelection().stream().map(Entity::getName).collect(Collectors.joining("")));
            s = StringUtils.replace(s, "%default%", event.getDefaultEntity().getName());
            s = StringUtils.replace(s, "%player%", event instanceof GamePlayerEvent ? ((GamePlayerEvent) event).getPlayer().getName() : "");
            s = StringUtils.replace(s, "%victim%", event instanceof DamageEvent ? ((DamageEvent) event).getVictim().getName() : event instanceof KillEvent ? ((KillEvent) event).getVictim().getName() : "");
            s = StringUtils.replace(s, "%damager%", event instanceof DamageEvent ? ((DamageEvent) event).getDamager().getName() : "");
            s = StringUtils.replace(s, "%killer%", event instanceof KillEvent ? ((KillEvent) event).getKiller().getName() : "");
            s = StringUtils.replace(s, "%shooter%", event instanceof DamageEvent ? ((DamageEvent) event).getShooter().getName() : "");
            s = StringUtils.replace(s, "%entity%", event instanceof EntityEvent ? ((EntityEvent) event).getEntity().getName() : "");
            return s;
        }
    }

    public void sortItems() {
        ItemStack items = this.inventory.getContents()
    }

    public abstract ItemStack getIcon();
    public abstract String getName();
    public abstract Inventory getInventory();
    public abstract ActionType getType();

    public abstract void execute (GameEvent event);

}
