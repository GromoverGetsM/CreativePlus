package ru.rstudios.creativeplus.creative.coding.events;

import org.bukkit.entity.Entity;
import ru.rstudios.creativeplus.creative.plots.Plot;

public interface GameEvent {

    Plot getPlot();

    Entity getDefaultEntity();

}
