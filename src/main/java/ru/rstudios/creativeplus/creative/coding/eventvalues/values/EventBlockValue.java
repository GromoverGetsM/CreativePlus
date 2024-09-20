package ru.rstudios.creativeplus.creative.coding.eventvalues.values;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.events.BlockEvent;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;
import ru.rstudios.creativeplus.creative.coding.eventvalues.ItemStackValue;

public class EventBlockValue extends ItemStackValue {

    public EventBlockValue () {}

    @Override
    public ItemStack get(GameEvent event, Entity entity) {
        return event instanceof BlockEvent ? new ItemStack(((BlockEvent) event).getBlock().getType()) : new ItemStack(Material.AIR, 0);
    }

}
