package ru.rstudios.creativeplus.creative.menus.coding;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class CreativeHolder implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(this, 9, "Sys");
    }
}
