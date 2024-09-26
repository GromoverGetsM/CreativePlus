package ru.rstudios.creativeplus.creative.coding;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.rstudios.creativeplus.creative.coding.starters.block.BlockDispenseStarter;
import ru.rstudios.creativeplus.creative.coding.starters.player.*;
import ru.rstudios.creativeplus.creative.plots.Plot;
import ru.rstudios.creativeplus.player.PlayerInfo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Random;

public class GlobalEventListener implements Listener {

    public static boolean skipDoubleClickCall (Player player, PlayerInteractEvent event) {
        PlayerInfo info = PlayerInfo.getPlayerInfo(player);

        if (info != null) {
            LocalDateTime time = info.getLastInteractTime();
            PlayerInteractEvent lastEvent = info.getLastInteractEvent();
            LocalDateTime now = LocalDateTime.now();

            if (lastEvent != null && time != null) {
                if (Objects.equals(event.getHand(), lastEvent.getHand()) && Objects.equals(event.getItem(), lastEvent.getItem())) {
                    if (ChronoUnit.MILLIS.between(time, now) > 50L) {
                        info.setLastInteractTime(now);
                        info.setLastInteractEvent(event);
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    if (ChronoUnit.MILLIS.between(time, now) < 2L) {
                        return true;
                    } else {
                        info.setLastInteractTime(now);
                        info.setLastInteractEvent(event);
                        return false;
                    }
                }
            } else {
                info.setLastInteractTime(now);
                info.setLastInteractEvent(event);
                return false;
            }
        }

        return false;
    }

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
            plot.getHandler().sendStarter(new BlockDispenseStarter.Event(event.getBlock(), plot, event));
        }
    }

    @EventHandler
    public void onChatEvent (AsyncChatEvent event) {
        Player player = event.getPlayer();
        Plot plot = Plot.getByWorld(player.getWorld());

        if (plot != null && player.getWorld() != plot.getLinkedDevPlot().getWorld()) {
            plot.getHandler().sendStarter(new PlayerChattedStarter.Event(player, plot, event));
        }
    }

    @EventHandler
    public void onBlockIgnited (BlockIgniteEvent event) {
        Player player = event.getPlayer();
        Plot plot = Plot.getByWorld(event.getBlock().getWorld());

        if (plot != null) {
            // plot.getHandler().sendStarter();
        }
    }

    @EventHandler
    public void onPlayerInteract (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Plot plot = Plot.getByWorld(player.getWorld());

        if (plot != null && player.getWorld() != plot.getLinkedDevPlot().getWorld()) {
            switch (event.getAction()) {
                case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                    if (!skipDoubleClickCall(player, event)) plot.getHandler().sendStarter(new PlayerRightClickStarter.Event(player, plot, event));
                }
                case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> {
                    if (!skipDoubleClickCall(player, event)) plot.getHandler().sendStarter(new PlayerLeftClickStarter.Event(player, plot, event));
                }
                case PHYSICAL -> {
                    if (!skipDoubleClickCall(player, event)) plot.getHandler().sendStarter(new PlayerPhysicalInteractStarter.Event(player, plot, event));
                }
            }
        }
    }
}
