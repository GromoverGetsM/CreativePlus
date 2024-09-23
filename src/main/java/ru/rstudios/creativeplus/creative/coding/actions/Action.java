package ru.rstudios.creativeplus.creative.coding.actions;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.events.*;
import ru.rstudios.creativeplus.creative.coding.eventvalues.ItemStackValue;
import ru.rstudios.creativeplus.creative.coding.eventvalues.LocationValue;
import ru.rstudios.creativeplus.creative.coding.eventvalues.NumericValue;
import ru.rstudios.creativeplus.creative.coding.eventvalues.StringValue;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.utils.CodingHandleUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Action {

    private final String name;
    private final Inventory inventory;
    private final Starter starter;

    private ItemStack[] originalContents;
    private ItemStack[] nonNullItems;
    private ItemStack[] texts;
    private ItemStack[] numbers;
    private ItemStack[] locations;
    private ItemStack[] dynamicVariables;
    private ItemStack[] itemStackGameValues;
    private ItemStack[] otherItems;

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

    public static boolean isNullOrAir(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    public void initInventorySort() {
        this.originalContents = this.inventory.getContents();
        this.nonNullItems = Arrays.stream(this.inventory.getContents())
                .filter(item -> item != null && item.getType() != Material.AIR)
                .toArray(ItemStack[]::new);
        this.texts = new ItemStack[0];
        this.numbers = new ItemStack[0];
        this.locations = new ItemStack[0];
        this.dynamicVariables = new ItemStack[0];
        this.otherItems = new ItemStack[0];
        this.itemStackGameValues = new ItemStack[0];
        sort();
    }

    private void sort() {
        for (int i = 0; i < this.nonNullItems.length - 1; i++) {
            ItemStack item = this.nonNullItems[i];

            if (!isNullOrAir(item)) {
                switch (item.getType()) {
                    case BOOK -> {
                        this.texts = ArrayUtils.add(this.texts, item);
                        this.otherItems = ArrayUtils.add(this.otherItems, item);
                    }
                    case SLIME_BALL -> {
                        this.numbers = ArrayUtils.add(this.numbers, item);
                        this.otherItems = ArrayUtils.add(this.otherItems, item);
                    }
                    case PAPER -> {
                        this.locations = ArrayUtils.add(this.locations, item);
                        this.otherItems = ArrayUtils.add(this.otherItems, item);
                    }
                    case APPLE -> {
                        Object o = CodingHandleUtils.parseGameValue(item);
                        if (o instanceof StringValue) {
                            this.texts = ArrayUtils.add(this.texts, item);
                        }
                        if (o instanceof NumericValue) {
                            this.numbers = ArrayUtils.add(this.numbers, item);
                        }
                        if (o instanceof LocationValue) {
                            this.locations = ArrayUtils.add(this.locations, item);
                        }
                        if (o instanceof ItemStackValue) {
                            this.itemStackGameValues = ArrayUtils.add(this.itemStackGameValues, item);
                        }
                        this.otherItems = ArrayUtils.add(this.otherItems, item);
                    }
                    case MAGMA_CREAM -> {
                        this.dynamicVariables = ArrayUtils.add(this.dynamicVariables, item);
                        this.otherItems = ArrayUtils.add(this.otherItems, item);
                    }
                    default -> this.otherItems = ArrayUtils.add(this.otherItems, item);
                }
            }
        }
    }

    public ItemStack[] getOriginalContents() {
        return this.originalContents;
    }

    public ItemStack[] getNonNullItems() {
        return this.nonNullItems;
    }

    public ItemStack[] getTexts() {
        return this.texts;
    }

    public ItemStack[] getNumbers() {
        return this.numbers;
    }

    public ItemStack[] getLocations() {
        return this.locations;
    }

    public ItemStack[] getItemStackGameValues() {
        return this.itemStackGameValues;
    }

    public ItemStack[] getOtherItems() {
        return this.otherItems;
    }

    public ItemStack[] getDynamicVariables() {
        return this.dynamicVariables;
    }


    public abstract ItemStack getIcon();
    public abstract String getName();
    public abstract Inventory getInventory();
    public abstract ActionType getType();

    public abstract void execute (GameEvent event);

}
