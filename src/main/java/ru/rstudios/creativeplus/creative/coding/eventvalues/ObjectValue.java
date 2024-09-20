package ru.rstudios.creativeplus.creative.coding.eventvalues;

import org.bukkit.entity.Entity;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;

public abstract class ObjectValue implements Value {

    public ObjectValue() {}

    public abstract Object get(GameEvent event, Entity entity);

}
