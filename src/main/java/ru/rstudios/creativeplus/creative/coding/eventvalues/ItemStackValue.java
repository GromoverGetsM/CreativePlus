package ru.rstudios.creativeplus.creative.coding.eventvalues;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;

public abstract class ItemStackValue implements Value {

    public ItemStackValue () {}

    public abstract ItemStack get(GameEvent event, Entity entity);

}
