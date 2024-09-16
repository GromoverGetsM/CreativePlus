package ru.rstudios.creativeplus.creative.coding;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import ru.rstudios.creativeplus.creative.coding.starters.player.PlayerBreakBlockStarter;
import ru.rstudios.creativeplus.creative.coding.starters.player.PlayerJoinStarter;
import ru.rstudios.creativeplus.creative.coding.starters.player.PlayerQuitStarter;
import ru.rstudios.creativeplus.creative.plots.Plot;

public class GlobalEventListener implements Listener {

    @EventHandler
    public void onPlayerChangedWorld (PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World from = event.getFrom();
        World now = player.getWorld();

        Plot fromPlot = Plot.getByWorld(from);
        Plot nowPlot = Plot.getByWorld(now);

        if (nowPlot != null) {
            nowPlot.getHandler().sendStarter(new PlayerJoinStarter.Event(player, nowPlot, event));
        }

        if (fromPlot != null) {
            fromPlot.getHandler().sendStarter(new PlayerQuitStarter.Event(player, nowPlot, event));
        }
    }

    @EventHandler
    public void onBlockBreak (BlockBreakEvent event) {
        Player player = event.getPlayer();
        Plot plot = Plot.getByWorld(event.getBlock().getWorld());

        if (plot != null) {
            plot.getHandler().sendStarter(new PlayerBreakBlockStarter.Event(player, plot, event));
        }

    }
}
