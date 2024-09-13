package ru.rstudios.creativeplus.events;

import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.rstudios.creativeplus.creative.menus.CreativeSystemMenu;
import ru.rstudios.creativeplus.creative.menus.main.WorldsMenu;
import ru.rstudios.creativeplus.player.PlayerInfo;

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

    @EventHandler (priority = EventPriority.LOWEST)
    public void onInventoryClicked (InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() != null && event.getClickedInventory().getHolder() instanceof CreativeSystemMenu && !event.isCancelled()) {
            event.setCancelled(true);
        }
    }
}
