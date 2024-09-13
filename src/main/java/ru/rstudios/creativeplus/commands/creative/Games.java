package ru.rstudios.creativeplus.commands.creative;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.rstudios.creativeplus.creative.menus.main.WorldsMenu;

public class Games implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            player.openInventory(new WorldsMenu("Миры игроков").getInventory());
        }
        return true;
    }
}
