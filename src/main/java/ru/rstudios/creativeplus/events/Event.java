package ru.rstudios.creativeplus.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.reflections.Reflections;
import ru.rstudios.creativeplus.creative.menus.coding.actions.PlayerAction;
import ru.rstudios.creativeplus.creative.menus.coding.starters.PlayerEvent;
import ru.rstudios.creativeplus.creative.menus.main.WorldsMenu;
import ru.rstudios.creativeplus.creative.plots.DevPlot;
import ru.rstudios.creativeplus.creative.plots.Plot;
import ru.rstudios.creativeplus.player.PlayerInfo;
import ru.rstudios.creativeplus.utils.CodingHandleUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;

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
                plot.unload(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract (PlayerInteractEvent event) {
        Block target = event.getPlayer().getTargetBlockExact(5);

        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("world") && event.getAction().isRightClick() && event.getItem() != null && event.getItem().getType().equals(Material.DIAMOND)) {
            event.getPlayer().openInventory(new WorldsMenu("Миры игроков").getInventory());
        }

        if (event.getAction().isRightClick() && target != null && target.getType() == Material.OAK_WALL_SIGN && event.getPlayer().getWorld().getName().endsWith("_dev")) {
            event.setCancelled(true);
            switch (target.getRelative(BlockFace.SOUTH).getType()) {
                case DIAMOND_BLOCK -> event.getPlayer().openInventory(new PlayerEvent("Событие игрока").getInventory());
                case COBBLESTONE -> event.getPlayer().openInventory(new PlayerAction("Действие игрока").getInventory());
            }
        } else if (target.getType() == Material.CHEST) {
            event.setCancelled(true);
            Chest chest = (Chest) target.getState();

            if (!Objects.equals(chest.getBlockInventory().getItem(0), new ItemStack(Material.BARRIER))) {
                String actionDisplayName = ((Sign) target.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH).getState()).getLine(2);
                event.getPlayer().openInventory(CodingHandleUtils.loadChestInventory(event.getPlayer().getWorld(), event.getPlayer().getTargetBlockExact(5).getLocation(), actionDisplayName));
                chest.getBlockInventory().setItem(0, new ItemStack(Material.BARRIER));
            } else {
                event.getPlayer().sendMessage("§bCreative+ §8» §fКакой-то игрок §6уже взаимодействует §fс этим сундуком.");
            }
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
                plot.unload(true);
            }
        }

        if (from.endsWith("_dev") && destination.endsWith("_CraftPlot") && destination.replace("_CraftPlot", "_dev").equalsIgnoreCase(from)) {
            Plot.getByWorld(event.getPlayer().getWorld()).getHandler().parseCodeBlocks();
        }
    }

    @EventHandler
    public void onBlockBroken (BlockBreakEvent event) {
        if (event.getPlayer().getWorld().getName().endsWith("_dev")) {
            Block b = event.getBlock();
            Material type = b.getType();
            if (!DevPlot.getAllowedBlocks().contains(type)) event.setCancelled(true);

            if (DevPlot.getStarterBlocks().contains(type) || (type == Material.OAK_WALL_SIGN && DevPlot.getStarterBlocks().contains(b.getRelative(BlockFace.SOUTH).getType()))) {
                if (type == Material.OAK_WALL_SIGN) {
                    b = b.getRelative(BlockFace.SOUTH);
                }
                BlockVector3 pos1 = BlockVector3.at(b.getX(), b.getY(), b.getZ());
                BlockVector3 pos2 = BlockVector3.at(b.getX() - 124, b.getY() + 1, b.getZ() -1);

                CodingHandleUtils.setBlocks(BukkitAdapter.adapt(b.getWorld()), pos1, pos2);
            } else if (DevPlot.getActionBlocks().contains(type) || (type == Material.OAK_WALL_SIGN && DevPlot.getActionBlocks().contains(b.getRelative(BlockFace.SOUTH).getType()))) {
                if (type == Material.OAK_WALL_SIGN) {
                    type = b.getRelative(BlockFace.SOUTH).getType();
                    b = b.getRelative(BlockFace.SOUTH);
                }

                Block finalB = b;
                switch (type) {
                    case COBBLESTONE -> {
                        b.getRelative(BlockFace.NORTH).setType(Material.AIR);
                        b.setType(Material.AIR);
                        b.getRelative(BlockFace.UP).setType(Material.AIR);
                        b.getRelative(BlockFace.WEST).setType(Material.AIR);

                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            Location lastStringBlock = getLastStringBlock(finalB.getLocation());
                            CodingHandleUtils.moveBlocks(finalB.getLocation().add(-2, 0, 0), lastStringBlock.add(-1, 1, -1), BlockFace.EAST, 2);
                        }, 5L);
                    }
                    default -> {
                        return;
                    }
                }
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

            if ((DevPlot.getStarterBlocks().contains(block) && event.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.LIGHT_BLUE_STAINED_GLASS)) || (DevPlot.getActionBlocks().contains(block) && event.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.LIGHT_GRAY_STAINED_GLASS))) {
                Material relativeBlock;
                String blockName;
                switch (block) {
                    case DIAMOND_BLOCK -> {
                        relativeBlock = Material.DIAMOND_ORE;
                        blockName = "Событие игрока";
                    }
                    case COBBLESTONE -> {
                        relativeBlock = Material.STONE;
                        blockName = "Действие игрока";
                    }
                    default -> {
                        relativeBlock = Material.AIR;
                        blockName = "UnknownAction";
                    }
                }

                event.setCancelled(!place(event.getBlock().getLocation(), relativeBlock, blockName));
            } else if ((DevPlot.getStarterBlocks().contains(block) && !event.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.LIGHT_BLUE_STAINED_GLASS)) || (DevPlot.getActionBlocks().contains(block) && !event.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.LIGHT_GRAY_STAINED_GLASS))) {
                event.setCancelled(true);
            }
        }
    }

    private boolean place (Location mainBlock, Material additional, String actionName) {
        if (additional != null && additional != Material.AIR) {
            if (additional != Material.PISTON) {
                mainBlock.getBlock().getRelative(BlockFace.WEST).setType(additional);
            } else {
                Block relative = mainBlock.getBlock().getRelative(BlockFace.WEST);
                relative.setType(additional);
                Directional facing = (Directional) relative.getBlockData();
                facing.setFacing(BlockFace.WEST);
                relative.setBlockData(facing);

                Block piston = relative.getRelative(BlockFace.WEST, 2);
                if (piston.getType() != Material.AIR) {
                    Location pos1 = piston.getLocation().add(-1, 0, 0);
                    Location pos2 = getLastStringBlock(piston.getLocation());

                    Location moveTo = pos2.getBlock().getRelative(BlockFace.WEST, 2).getLocation();

                    if (Plot.getByWorld(Bukkit.getWorld(mainBlock.getWorld().getName().replace("_dev", "_CraftPlot"))).getLinkedDevPlot().inTerritory(moveTo)) {
                        CodingHandleUtils.moveBlocks(pos1, pos2, BlockFace.WEST, 2);
                    }
                }
            }
        }

        mainBlock.getBlock().getRelative(BlockFace.NORTH).setType(Material.OAK_WALL_SIGN);
        Sign sign = (Sign) mainBlock.getBlock().getRelative(BlockFace.NORTH).getState();
        sign.setLine(1, actionName);
        sign.update();

        return true;
    }

    private Location getLastStringBlock (Location starterBlock) {
        Block b = starterBlock.getBlock();

        for (int x = starterBlock.getBlockX(); x > -64; x -= 2) {
            Location cloned = starterBlock.clone().add(x, 0, 0);

            if (cloned.getBlock().getType() != Material.AIR && cloned.getBlockX() < b.getLocation().getBlockX()) {
                b = cloned.getBlock();
            }
        }

        return b.getLocation();
    }

    @EventHandler
    public void onPlayerChatted (AsyncChatEvent event) {
        Player player = event.getPlayer();
        String message = LegacyComponentSerializer.legacySection().serialize(event.message());
        if (message.length() > 256) message = message.substring(0, 1024);
        if (message.contains("&")) message.replace("&", "§");

        if (player.getWorld().getName().endsWith("_dev")) {
            ItemStack activeItem = player.getActiveItem();
            if (!activeItem.equals(new ItemStack(Material.AIR))) {
                switch (activeItem.getType()) {
                    case BOOK -> {
                        event.setCancelled(true);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                        player.sendTitle("§aЗначение установлено", message);
                        activeItem.getItemMeta().setDisplayName(message);
                    }
                    case SLIME_BALL -> {
                        event.setCancelled(true);

                        try {
                            double d = Double.parseDouble(message);
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                            player.sendTitle("§aЗначение установлено", String.valueOf(d));
                            activeItem.getItemMeta().setDisplayName(String.valueOf(d));
                        } catch (NumberFormatException e) {
                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0F, 1.0F);
                            player.sendTitle("§cНекорректное значение", "§6" + message);
                            activeItem.getItemMeta().setDisplayName(message);
                        }
                    }
                    case PAPER -> {

                    }
                }
            }
        }
    }
}
