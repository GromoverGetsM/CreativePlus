package ru.rstudios.creativeplus.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class UniversalMenuHolder implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(this, 9, "§cSystemInventory");
    }

}
