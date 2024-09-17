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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ru.rstudios.creativeplus.creative.coding.actions.ActionType;
import ru.rstudios.creativeplus.creative.menus.coding.*;
import ru.rstudios.creativeplus.creative.menus.coding.actions.GiveItems;
import ru.rstudios.creativeplus.creative.menus.coding.actions.PlayerAction;
import ru.rstudios.creativeplus.creative.menus.coding.actions.SendMessage;
import ru.rstudios.creativeplus.creative.menus.coding.starters.PlayerEvent;
import ru.rstudios.creativeplus.creative.menus.main.MyWorlds;
import ru.rstudios.creativeplus.creative.menus.main.WorldsMenu;
import ru.rstudios.creativeplus.creative.plots.DevPlot;
import ru.rstudios.creativeplus.creative.plots.Plot;
import ru.rstudios.creativeplus.creative.plots.PlotInitializeReason;
import ru.rstudios.creativeplus.player.PlayerInfo;
import ru.rstudios.creativeplus.utils.CodingHandleUtils;
import ru.rstudios.creativeplus.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
            if (player.getWorld().getName().endsWith("_dev")) {
                PersistentDataContainer pdc = player.getPersistentDataContainer();
                NamespacedKey isInDev = new NamespacedKey(plugin, "CodingActive");
                NamespacedKey handlingPaperKey = new NamespacedKey(plugin, "HandlingPaper");
                pdc.set(isInDev, PersistentDataType.BOOLEAN, false);
                pdc.set(handlingPaperKey, PersistentDataType.BOOLEAN, false);
            }
            Plot plot = Plot.getByPlayer(event.getPlayer());
            if (plot != null) plot.getHandler().parseCodeBlocks();
            if (plot == null || plot.getPlotOnline() == 0) {
                plot.unload(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick (InventoryClickEvent event) throws InstantiationException, IllegalAccessException {
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() != null) {
            if (event.getClickedInventory().getHolder() instanceof AbstractCategoryMenu) {
                event.setCancelled(true);
                Block targetBlock = player.getTargetBlockExact(5);
                if (event.getCurrentItem() != null && targetBlock != null && targetBlock.getType() == Material.OAK_WALL_SIGN) {
                    Sign sign = (Sign) targetBlock.getState();
                    String itemDisplayName = event.getCurrentItem().getItemMeta().getDisplayName();
                    sign.setLine(2, ActionType.getByDisplayName(ChatColor.stripColor(itemDisplayName).trim()).getName());
                    sign.update();
                    if (ActionType.getByDisplayName(ChatColor.stripColor(itemDisplayName).trim()).getNeedChest()) {
                        Block chest = targetBlock.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP);
                        chest.setType(Material.CHEST);
                        File chestsFolder = new File(Bukkit.getWorldContainer() + File.separator + chest.getWorld().getName() + File.separator + "chests");
                        File chestFileR = new File(chestsFolder, chest.getLocation() + ".txt");
                        if (chestFileR.exists()) chestFileR.delete();
                        try {
                            FileUtil.createNewFile(chestsFolder, chest.getLocation() + ".txt");
                        } catch (IOException e) {
                            plugin.getLogger().severe(e.getLocalizedMessage());
                        }
                    }
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                }
            }

            if (event.getClickedInventory().getHolder() instanceof AbstractSelectCategoryMenu) {
                if (event.getCurrentItem() != null && !Objects.equals(event.getCurrentItem(), new ItemStack(Material.AIR))) {
                    List<Integer> slotsForCategory = Arrays.asList(10, 12, 14, 16, 37, 39, 41, 43);

                    if (slotsForCategory.contains(event.getSlot())) {
                        ItemStack item = event.getCurrentItem();
                        ItemMeta meta = item.getItemMeta();
                        String displayName = meta.hasDisplayName() ? ChatColor.stripColor(meta.getDisplayName()).trim() : "Коммуникация";

                        CodingCategoryType categoryType = CodingCategoryType.getByDisplayName(displayName);
                        if (categoryType != null) {
                            player.openInventory(categoryType.categoryClass.newInstance().getInventory());
                        }
                    }
                }
            }

            if (event.getClickedInventory().getHolder() instanceof Variables) {
                event.setCancelled(true);
                if (event.getCurrentItem() != null) {
                    player.getInventory().addItem(event.getCurrentItem());
                }
            }

            if (event.getClickedInventory().getHolder() instanceof WorldsMenu) {
                event.setCancelled(true);
                if (event.getSlot() == 49) {
                    MyWorlds mw = new MyWorlds("Мои миры");
                    mw.setPlayer((Player) event.getWhoClicked());
                    event.getWhoClicked().openInventory(mw.getInventory());
                } else if (Arrays.asList(20, 21, 22, 23, 24, 29, 30, 31, 32, 33).contains(event.getSlot())) {
                    if (event.getCurrentItem() != null) {
                        List<String> lore = event.getCurrentItem().getItemMeta().getLore();
                        int id = Integer.parseInt(lore.get(lore.size() - 2).split(":")[1].trim().substring(2));
                        Plot plot = Plot.getById(id);
                        if (plot != null) {
                            event.getWhoClicked().closeInventory();
                            if (!plot.getPlotLoaded()) plot.load(plot.getPlotName());
                            Plot.teleportToPlot(plot, (Player) event.getWhoClicked());
                        }
                    }
                }
            }

            if (event.getClickedInventory().getHolder() instanceof MyWorlds) {
                if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() != null && event.getClickedInventory().getHolder() instanceof MyWorlds) {
                    if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.WHITE_STAINED_GLASS) {
                        if (event.isCancelled()) return;
                        event.setCancelled(true);
                        event.getWhoClicked().closeInventory();
                        new Plot(Plot.getNextPlotName(), event.getWhoClicked().getName(), PlotInitializeReason.PLAYER_PLOT_CREATED);
                    } else if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.WHITE_STAINED_GLASS) {
                        List<String> lore = event.getCurrentItem().getItemMeta().getLore();
                        int id = Integer.parseInt(lore.get(lore.size() - 2).split(":")[1].trim().substring(2));
                        Plot plot = Plot.getById(id);
                        if (plot != null) {
                            event.getWhoClicked().closeInventory();
                            if (!plot.getPlotLoaded()) plot.load(plot.getPlotName());
                            Plot.teleportToPlot(plot, (Player) event.getWhoClicked());
                        }
                        event.setCancelled(true);
                    }
                }
            }

            if (event.getClickedInventory().getHolder() instanceof SendMessage || event.getClickedInventory().getHolder() instanceof GiveItems) {
                if (Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8).contains(event.getSlot()) || Arrays.asList(44, 43, 42, 41, 40, 39, 38, 37, 36).contains(event.getSlot())) {
                    event.setCancelled(true);
                }
            }


        }
    }

    @EventHandler
    public void onInventoryClose (InventoryCloseEvent event) {
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof CodingSystemMenu) {
            Player player = (Player) event.getPlayer();
            Block chest = player.getTargetBlockExact(5);

            if (chest != null && chest.getType() == Material.CHEST) {
                CodingHandleUtils.saveInventoryToChest(player.getWorld(), chest.getLocation(), event.getInventory());
            }
        }
    }

    @EventHandler
    public void onPlayerInteract (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block target = player.getTargetBlockExact(5);

        if (event.getItem() != null && !Objects.equals(event.getItem(), new ItemStack(Material.AIR)) && event.getItem().getType() == Material.PAPER) {
            Plot plot = Plot.getByWorld(player.getWorld());
            PersistentDataContainer pdc = event.getPlayer().getPersistentDataContainer();
            NamespacedKey isInDev = new NamespacedKey(plugin, "CodingActive");
            NamespacedKey handlingPaperKey = new NamespacedKey(plugin, "HandlingPaper");

            if (pdc.has(isInDev, PersistentDataType.BOOLEAN) && Boolean.TRUE.equals(pdc.get(isInDev, PersistentDataType.BOOLEAN))) {
                ItemStack item = event.getItem();
                ItemMeta meta = item.getItemMeta();

                switch (event.getAction()) {
                    case LEFT_CLICK_AIR -> {
                        if (player.getWorld() == plot.getLinkedDevPlot().getWorld()) {
                            pdc.set(handlingPaperKey, PersistentDataType.BOOLEAN, true);
                            player.teleport(plot.getPlotWorld().getSpawnLocation());
                        } else {
                            pdc.set(handlingPaperKey, PersistentDataType.BOOLEAN, false);
                            player.teleport(plot.getLinkedDevPlot().getWorld().getSpawnLocation());
                        }
                    }
                    case RIGHT_CLICK_AIR -> {
                        if (player.getWorld() == plot.getPlotWorld()) {
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                            player.sendTitle("§aЗначение установлено", player.getLocation().toString());
                            meta.setDisplayName(player.getLocation().toString());
                        }
                    }
                    case RIGHT_CLICK_BLOCK -> {
                        if (player.getWorld() == plot.getPlotWorld() && target != null) {
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                            player.sendTitle("§aЗначение установлено", target.getLocation().toString());
                            meta.setDisplayName(target.getLocation().toString());
                        }
                    }
                }

                item.setItemMeta(meta);
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (player.getWorld().getName().endsWith("_dev")) {

                ItemStack itemInHand = player.getInventory().getItemInMainHand();
                if (itemInHand != null && itemInHand.getType() == Material.IRON_INGOT) {

                    player.openInventory(new Variables("Переменные").getInventory());

                    return;
                }
            }
        }

        if (player.getWorld().getName().equalsIgnoreCase("world") && event.getAction().isRightClick() && event.getItem() != null && event.getItem().getType().equals(Material.DIAMOND)) {
            player.openInventory(new WorldsMenu("Миры игроков").getInventory());
        }

        if (event.getAction().isRightClick() && target != null && target.getType() == Material.OAK_WALL_SIGN && player.getWorld().getName().endsWith("_dev")) {
            event.setCancelled(true);

            switch (target.getRelative(BlockFace.SOUTH).getType()) {
                case DIAMOND_BLOCK -> player.openInventory(new PlayerEvent("Событие игрока").getInventory());
                case COBBLESTONE -> player.openInventory(new PlayerAction("Действие игрока").getInventory());
            }
        } else if (event.getAction().isRightClick() && target != null && target.getType() == Material.CHEST && player.getWorld().getName().endsWith("_dev")) {
            event.setCancelled(true);
            Chest chest = (Chest) target.getState();

            if (!Objects.equals(chest.getBlockInventory().getItem(0), new ItemStack(Material.BARRIER))) {
                String actionDisplayName = ((Sign) target.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH).getState()).getLine(2);
                player.openInventory(CodingHandleUtils.loadChestInventory(player.getWorld(), target.getLocation(), actionDisplayName));
                chest.getBlockInventory().setItem(0, new ItemStack(Material.BARRIER));
            } else {
                player.sendMessage("§bCreative+ §8» §fКакой-то игрок §6уже взаимодействует §fс этим сундуком.");
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
    public void onWorldChanged(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String from = event.getFrom().getName();
        String destination = player.getWorld().getName();
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        NamespacedKey isInDev = new NamespacedKey(plugin, "CodingActive");
        NamespacedKey handlingPaperKey = new NamespacedKey(plugin, "HandlingPaper");

        if (destination.endsWith("_dev")) {
            pdc.set(isInDev, PersistentDataType.BOOLEAN, true);
        }

        if (from.endsWith("_dev") && !destination.endsWith("_dev")) {
            boolean isMovingToLinkedCraftPlot = from.replace("_dev", "_CraftPlot").equalsIgnoreCase(destination);

            boolean handlingPaper = pdc.has(handlingPaperKey, PersistentDataType.BOOLEAN) &&
                    pdc.get(handlingPaperKey, PersistentDataType.BOOLEAN);

            if (!isMovingToLinkedCraftPlot || !handlingPaper) {
                pdc.set(isInDev, PersistentDataType.BOOLEAN, false);
                pdc.set(handlingPaperKey, PersistentDataType.BOOLEAN, false);

                Plot.getByWorld(event.getFrom()).getHandler().parseCodeBlocks();
            }
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
    public void onPlayerChatted(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String message = LegacyComponentSerializer.legacySection().serialize(event.message());
        if (message.length() > 256) message = message.substring(0, 256);
        if (message.contains("&")) message = message.replace("&", "§");

        if (player.getWorld().getName().endsWith("_dev")) {
            ItemStack activeItem = player.getInventory().getItemInMainHand();
            ItemMeta meta = activeItem.getItemMeta();

            if (activeItem != null && activeItem.getType() != Material.AIR) {

                switch (activeItem.getType()) {
                    case BOOK -> {
                        event.setCancelled(true);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                        player.sendTitle("§aЗначение установлено", message, 10, 70, 20);

                        meta = activeItem.getItemMeta();
                        if (meta != null) {
                            meta.setDisplayName(message);
                        }
                    }
                    case SLIME_BALL -> {
                        event.setCancelled(true);

                        try {
                            double d = Double.parseDouble(message);
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                            player.sendTitle("§aЗначение установлено", String.valueOf(d), 10, 70, 20);

                            meta = activeItem.getItemMeta();
                            if (meta != null) {
                                meta.setDisplayName(String.valueOf(d));
                            }
                        } catch (NumberFormatException e) {
                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0F, 1.0F);
                            player.sendTitle("§cНекорректное значение", "§6" + message, 10, 70, 20);

                            meta = activeItem.getItemMeta();
                            if (meta != null) {
                                meta.setDisplayName(message);
                            }
                        }
                    }
                }

                activeItem.setItemMeta(meta);
                player.getInventory().setItemInMainHand(activeItem);
            }
        }
    }

}
