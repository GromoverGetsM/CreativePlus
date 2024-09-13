package ru.rstudios.creativeplus.commands.creative;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.rstudios.creativeplus.creative.plots.Plot;

public class devCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                Plot plot = Plot.getByPlayer(player);
                if (plot != null && (plot.getAllowedDevs().contains(player.getName()) || plot.getOwner().equalsIgnoreCase(player.getName()))) {
                    plot.teleportToDev(player);
                }
            } else if (args.length == 1) {
                Plot plot = Plot.getByPlayer(player);
                if (plot != null && (plot.getAllowedDevs().contains(player.getName()) || plot.getOwner().equalsIgnoreCase(player.getName()))) {
                    plot.addAllowedDev(args[0]);
                }
            }
        }

        return true;
    }
}
