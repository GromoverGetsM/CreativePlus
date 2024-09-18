package ru.rstudios.creativeplus.creative.coding.eventvalues;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;

public abstract class LocationValue implements Value {

    public LocationValue() {}

    public abstract Location get(GameEvent event, Entity entity);

}
