package ru.rstudios.creativeplus.creative.coding.events;

import org.bukkit.entity.Entity;

public interface KillEvent {

    Entity getKiller();
    Entity getVictim();

}
