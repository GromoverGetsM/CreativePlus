package ru.rstudios.creativeplus.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.rstudios.creativeplus.creative.menus.main.WorldsMenu;
import ru.rstudios.creativeplus.creative.plots.DevPlot;
import ru.rstudios.creativeplus.creative.plots.Plot;
import ru.rstudios.creativeplus.player.PlayerInfo;
import ru.rstudios.creativeplus.utils.CodingHandleUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class Event implements Listener {

    @EventHandler
    public void onPlayerJoin (PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerInfo.getPlayerInfo(player);

        if (player.getWorld().getName().equalsIgnoreCase("world")) {
            player.getInventory().clear();
            ItemStack createWorld = new ItemStack(Material.DIAMOND, 1);
            ItemMeta createWorldMeta = createWorld.getItemMeta();
            createWorldMeta.setDisplayName("§r §e§lСОЗДАТЬ МИР §r");
            createWorld.setItemMeta(createWorldMeta);
            player.getInventory().setItem(4, createWorld);
        }
    }

    @EventHandler
    public void onPlayerLeft (PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerInfo.removePlayer(player);

        if (player.getWorld().getName().endsWith("_dev") || player.getWorld().getName().endsWith("_CraftPlot")) {
            Plot plot = Plot.getByPlayer(event.getPlayer());
            if (plot == null || plot.getPlotOnline() == 0) {
                plot.unload();
            }
        }
    }

    @EventHandler
    public void onPlayerInteract (PlayerInteractEvent event) {

        if (event.getAction().isRightClick() && event.getItem() != null && event.getItem().getType().equals(Material.DIAMOND)) {
            event.getPlayer().openInventory(new WorldsMenu("Миры игроков").getInventory());
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            WorldBorder border = event.getPlayer().getWorld().getWorldBorder();

            if (!border.isInside(event.getTo())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onWorldChanged (PlayerChangedWorldEvent event) {
        String from = event.getFrom().getName();
        String destination = event.getPlayer().getWorld().getName();
        boolean inPlot;

        if (
                (from.endsWith("_CraftPlot") && destination.endsWith("_dev") && from.replace("_CraftPlot", "_dev").equalsIgnoreCase(destination)) ||
                        (from.endsWith("_dev") && destination.endsWith("_CraftPlot") && destination.replace("_CraftPlot", "_dev").equalsIgnoreCase(from))
        ) inPlot = true;
        else inPlot = !from.endsWith("_dev") && !from.endsWith("_CraftPlot");

        if (!inPlot) {
            Plot plot = Plot.getByWorld(event.getFrom());
            if (plot == null || plot.getPlotOnline() == 0) {
                plot.unload();
            }
        }
    }

    @EventHandler
    public void onBlockBroken (BlockBreakEvent event) {
        if (event.getPlayer().getWorld().getName().endsWith("_dev")) {
            Block b = event.getBlock();
            Material type = b.getType();
            if (!DevPlot.getAllowedBlocks().contains(type)) event.setCancelled(true);

            if (DevPlot.getStarterBlocks().contains(type)) {
                BlockVector3 pos1 = BlockVector3.at(b.getX(), b.getY(), b.getZ());
                BlockVector3 pos2 = BlockVector3.at(b.getX() - 124, b.getY() + 1, b.getZ() -1);

                if (WorldEditPlugin.getPlugin(WorldEditPlugin.class).isEnabled()) plugin.getLogger().warning("WorldEdit enabled");
                else {
                    plugin.getLogger().severe("WorldEdit Disabled. Return");
                    return;
                }

                CodingHandleUtils.setBlocks(new BukkitWorld(b.getWorld()), pos1, pos2);
            }
        }
    }

    @EventHandler
    public void onBlockPlaced (BlockPlaceEvent event) {
        if (event.getPlayer().getWorld().getName().endsWith("_dev")) {
            Material block = event.getBlock().getType();
            List<Material> allowedBlock = DevPlot.getAllowedBlocks();

            if (!allowedBlock.contains(block)) event.setCancelled(true);
            else if (DevPlot.getStarterBlocks().contains(block) && !event.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.LIGHT_BLUE_STAINED_GLASS)) event.setCancelled(true);
            else if (DevPlot.getActionBlocks().contains(block) && !event.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.LIGHT_GRAY_STAINED_GLASS)) event.setCancelled(true);
        }
    }
}
