package ru.rstudios.creativeplus.commands.creative;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.jetbrains.annotations.NotNull;
import ru.rstudios.creativeplus.creative.coding.starters.player.PlayerJoinStarter;
import ru.rstudios.creativeplus.creative.plots.Plot;

public class playCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                Plot plot = Plot.getByPlayer(player);
                World world = player.getWorld();

                if (plot != null) {
                    Plot.teleportToPlot(plot, player);

                    if (world != plot.getLinkedDevPlot().getWorld()) plot.getHandler().sendStarter(new PlayerJoinStarter.Event(player, plot, new PlayerChangedWorldEvent(player, world)));
                }
            }
        }

        return true;
    }

}
