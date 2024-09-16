package ru.rstudios.creativeplus.creative.coding;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import ru.rstudios.creativeplus.creative.coding.starters.block.BlockDispenseStarter;
import ru.rstudios.creativeplus.creative.coding.starters.player.PlayerBreakBlockStarter;
import ru.rstudios.creativeplus.creative.coding.starters.player.PlayerJoinStarter;
import ru.rstudios.creativeplus.creative.coding.starters.player.PlayerPlaceBlockStarter;
import ru.rstudios.creativeplus.creative.coding.starters.player.PlayerQuitStarter;
import ru.rstudios.creativeplus.creative.plots.Plot;

import java.util.Random;

public class GlobalEventListener implements Listener {

    @EventHandler
    public void onPlayerChangedWorld (PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World from = event.getFrom();
        World now = player.getWorld();

        Plot fromPlot = Plot.getByWorld(from);
        Plot nowPlot = Plot.getByWorld(now);

        if (nowPlot != null && nowPlot.getLinkedDevPlot().getWorld() != now) {
            nowPlot.getHandler().sendStarter(new PlayerJoinStarter.Event(player, nowPlot, event));
        }

        if (fromPlot != null && fromPlot.getLinkedDevPlot().getWorld() != from) {
            fromPlot.getHandler().sendStarter(new PlayerQuitStarter.Event(player, nowPlot, event));
        }
    }

    @EventHandler
    public void onBlockBreak (BlockBreakEvent event) {
        Player player = event.getPlayer();
        Plot plot = Plot.getByWorld(event.getBlock().getWorld());

        if (plot != null && plot.getLinkedDevPlot().getWorld() != event.getBlock().getWorld()) {
            plot.getHandler().sendStarter(new PlayerBreakBlockStarter.Event(player, plot, event));
        }

    }

    @EventHandler
    public void onBlockPlace (BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Plot plot = Plot.getByWorld(event.getBlock().getWorld());

        if (plot != null && plot.getLinkedDevPlot().getWorld() != event.getBlock().getWorld()) {
            plot.getHandler().sendStarter(new PlayerPlaceBlockStarter.Event(player, plot, event));
        }
    }

    @EventHandler
    public void onBlockDispense (BlockDispenseEvent event) {
        Plot plot = Plot.getByWorld(event.getBlock().getWorld());

        if (plot != null && plot.getLinkedDevPlot().getWorld() != event.getBlock().getWorld()) {
            plot.getHandler().sendStarter(new BlockDispenseStarter.Event(plot.getPlotOnlineList().get(new Random().nextInt(0, plot.getPlotOnlineList().size())), plot, event));
        }
    }
}
