package ru.rstudios.creativeplus.events;

import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.rstudios.creativeplus.creative.menus.main.WorldsMenu;
import ru.rstudios.creativeplus.player.PlayerInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        if (from.endsWith("_dev") && destination.endsWith("_CraftPlot") && !from.replace("_dev", "_CraftPlot").equals(destination)) {

        } else if (from.endsWith("_CraftPlot") && destination.endsWith("_dev") && !destination.replace("_dev", "_CraftPlot").equals(from)) {

        } else if ((from.endsWith("_CraftPlot") || from.endsWith("_dev")) && (!destination.endsWith("_CraftPlot") || !destination.endsWith("_dev"))) {

        }
    }

    @EventHandler
    public void onBlockBroken (BlockBreakEvent event) {
        if (event.getPlayer().getWorld().getName().endsWith("_dev")) {
            if (Arrays.asList(Material.WHITE_STAINED_GLASS, Material.LIGHT_BLUE_STAINED_GLASS, Material.LIGHT_GRAY_STAINED_GLASS).contains(event.getBlock().getType())) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlaced (BlockPlaceEvent event) {
        if (event.getPlayer().getWorld().getName().endsWith("_dev")) {
            List<Material> allowedBlock = new ArrayList<>();
            allowedBlock.add(Material.ENDER_CHEST);
            allowedBlock.add(Material.CRAFTING_TABLE);
            allowedBlock.add(Material.SHULKER_BOX);
            allowedBlock.add(Material.ANVIL);

            if (!allowedBlock.contains(event.getBlock().getType())) event.setCancelled(true);
        }
    }
}
