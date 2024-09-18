package ru.rstudios.creativeplus.creative.coding.eventvalues.values;

import org.bukkit.entity.Entity;
import ru.rstudios.creativeplus.creative.coding.events.ChatEvent;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;
import ru.rstudios.creativeplus.creative.coding.eventvalues.StringValue;

public class EventMessage extends StringValue {

    public EventMessage() {}

    @Override
    public String get(GameEvent event, Entity entity) {
        return event instanceof ChatEvent ? ((ChatEvent) event).getMessage() : "";
    }

}
