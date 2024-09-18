package ru.rstudios.creativeplus.creative.coding.eventvalues;

import org.bukkit.entity.Entity;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;

public interface Value {

    Object get(GameEvent event, Entity entity);

}
