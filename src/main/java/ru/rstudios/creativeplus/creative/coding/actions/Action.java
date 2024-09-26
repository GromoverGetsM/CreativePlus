package ru.rstudios.creativeplus.creative.coding.actions;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.dynamicvariables.DynamicVariable;
import ru.rstudios.creativeplus.creative.coding.events.*;
import ru.rstudios.creativeplus.creative.coding.eventvalues.ItemStackValue;
import ru.rstudios.creativeplus.creative.coding.eventvalues.LocationValue;
import ru.rstudios.creativeplus.creative.coding.eventvalues.NumericValue;
import ru.rstudios.creativeplus.creative.coding.eventvalues.StringValue;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.utils.CodingHandleUtils;

import java.util.Arrays;
import java.util.LinkedList;
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

    protected List<Entity> selection;

    public Action (Starter starter, String name, Inventory inventory) {
        this.name = name;
        this.inventory = inventory;
        this.starter = starter;
    }

    public String replacePlaceholders (String s, GameEvent event, Entity entity) {
        if (s == null || s.isEmpty()) {
            return null;
        } else {
            if (s.contains("%selected%")) s = StringUtils.replace(s, "%selected%", entity.getName());
            if (s.contains("%default%")) s = StringUtils.replace(s, "%default%", event.getDefaultEntity().getName());
            if (s.contains("%player%")) s = StringUtils.replace(s, "%player%", event instanceof GamePlayerEvent ? ((GamePlayerEvent) event).getPlayer().getName() : "");
            if (s.contains("%victim%")) s = StringUtils.replace(s, "%victim%", event instanceof DamageEvent ? ((DamageEvent) event).getVictim().getName() : event instanceof KillEvent ? ((KillEvent) event).getVictim().getName() : "");
            if (s.contains("%damager%")) s = StringUtils.replace(s, "%damager%", event instanceof DamageEvent ? ((DamageEvent) event).getDamager().getName() : "");
            if (s.contains("%killer%")) s = StringUtils.replace(s, "%killer%", event instanceof KillEvent ? ((KillEvent) event).getKiller().getName() : "");
            if (s.contains("%shooter%")) s = StringUtils.replace(s, "%shooter%", event instanceof DamageEvent ? ((DamageEvent) event).getShooter().getName() : "");
            if (s.contains("%entity%")) s = StringUtils.replace(s, "%entity%", event instanceof EntityEvent ? ((EntityEvent) event).getEntity().getName() : "");
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
        this.itemStackGameValues = new ItemStack[0];
        sort();
    }

    private void sort() {
        for (int i = 0; i < this.nonNullItems.length; i++) {
            ItemStack item = this.nonNullItems[i];

            if (!isNullOrAir(item)) {
                switch (item.getType()) {
                    case BOOK -> this.texts = ArrayUtils.add(this.texts, item);
                    case SLIME_BALL -> this.numbers = ArrayUtils.add(this.numbers, item);
                    case PAPER -> this.locations = ArrayUtils.add(this.locations, item);
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
                    }
                    case MAGMA_CREAM -> {
                        this.dynamicVariables = ArrayUtils.add(this.dynamicVariables, item);
                        this.texts = ArrayUtils.add(this.texts, item);
                        this.numbers = ArrayUtils.add(this.numbers, item);
                        this.locations = ArrayUtils.add(this.locations, item);
                    }
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

    public ItemStack[] getDynamicVariables() {
        return this.dynamicVariables;
    }

    public List<String> getAsTexts(GameEvent event, Entity entity) {
        List<String> list = new LinkedList<>();

        for (ItemStack item : this.texts) {
            switch (item.getType()) {
                case BOOK -> {
                    if (item.getItemMeta().hasDisplayName()) {
                        list.add(item.getItemMeta().getDisplayName());
                    } else {
                        list.add(item.getI18NDisplayName());
                    }
                }
                case APPLE -> {
                    Object o = CodingHandleUtils.parseGameValue(item);
                    if (o instanceof StringValue) {
                        list.add(((StringValue) o).get(event, entity));
                    }
                }
                case MAGMA_CREAM -> {
                    if (item.getItemMeta().hasDisplayName()) {
                        String displayName = item.getItemMeta().getDisplayName();
                        displayName = this.replacePlaceholders(displayName, event, entity);

                        list.add(new DynamicVariable(ChatColor.stripColor(displayName)).getValue(event.getPlot()) == null ? "" : new DynamicVariable(ChatColor.stripColor(displayName)).getValue(event.getPlot()).toString());
                    }
                }
            }
        }

        return list;
    }


    public abstract ItemStack getIcon();
    public abstract String getName();
    public abstract Inventory getInventory();
    public abstract ActionType getType();

    public abstract void execute (GameEvent event);

}
