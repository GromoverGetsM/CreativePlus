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
import ru.rstudios.creativeplus.creative.coding.starters.StarterType;
import ru.rstudios.creativeplus.creative.menus.coding.*;
import ru.rstudios.creativeplus.creative.menus.coding.actions.ActionVar;
import ru.rstudios.creativeplus.creative.menus.coding.actions.GameAction;
import ru.rstudios.creativeplus.creative.menus.coding.actions.PlayerAction;
import ru.rstudios.creativeplus.creative.menus.coding.actions.ifPlayer;
import ru.rstudios.creativeplus.creative.menus.coding.starters.PlayerEvent;
import ru.rstudios.creativeplus.creative.menus.main.MyWorlds;
import ru.rstudios.creativeplus.creative.menus.main.WorldsMenu;
import ru.rstudios.creativeplus.creative.plots.DevPlot;
import ru.rstudios.creativeplus.creative.plots.Plot;
import ru.rstudios.creativeplus.creative.plots.PlotInitializeReason;
import ru.rstudios.creativeplus.player.PlayerInfo;
import ru.rstudios.creativeplus.utils.CodingHandleUtils;
import ru.rstudios.creativeplus.utils.FileUtil;
import ru.rstudios.creativeplus.utils.LoadInventoryReason;

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
                if (plot != null) {
                    plot.getHandler().saveDynamicVariables();
                    plot.unload(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event) throws InstantiationException, IllegalAccessException {
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().getHolder() == null) return;

        Object holder = event.getClickedInventory().getHolder();
        ItemStack currentItem = event.getCurrentItem();

        if (holder instanceof AbstractCategoryMenu) {
            event.setCancelled(true);
            handleCategoryMenuClick(player, currentItem);
        }

        else if (holder instanceof AbstractSelectCategoryMenu) {
            event.setCancelled(true);
            handleSelectCategoryMenuClick(event, player, currentItem);
        }

        else if (holder instanceof Variables) {
            event.setCancelled(true);
            if (currentItem != null) player.getInventory().addItem(currentItem);
        }

        else if (holder instanceof WorldsMenu) {
            event.setCancelled(true);
            handleWorldsMenuClick(event, player, currentItem);
        }

        else if (holder instanceof MyWorlds) {
            handleMyWorldsClick(event, currentItem);
        }

        else if (holder instanceof CodingSystemMenu) {
            event.setCancelled(((CodingSystemMenu) holder).getDisallowedSlots().contains(event.getSlot()));
        }

        else if (holder instanceof GameValues) {
            event.setCancelled(true);
            handleGameValueClick(player, currentItem);
        }
    }

    private void handleGameValueClick (Player player, ItemStack currentItem) {
        if (currentItem == null) return;

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.APPLE && currentItem.getItemMeta().hasDisplayName()) {
            String name = ChatColor.stripColor(currentItem.getItemMeta().getDisplayName()).trim();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name);
            item.setItemMeta(meta);
            player.closeInventory();
        }
    }

    private void handleCategoryMenuClick(Player player, ItemStack currentItem) {
        if (currentItem == null) return;

        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock != null && targetBlock.getType() == Material.OAK_WALL_SIGN) {
            Sign sign = (Sign) targetBlock.getState();
            String itemDisplayName = ChatColor.stripColor(currentItem.getItemMeta().getDisplayName()).trim();
            ActionType actionType = ActionType.getByDisplayName(itemDisplayName);
            StarterType starterType = actionType == null ? StarterType.getByDisplayName(itemDisplayName) : null;

            if (actionType != null || starterType != null) {
                sign.setLine(2, actionType != null ? actionType.getName() : starterType.getName());
                sign.update();

                if (actionType != null && actionType.getNeedChest()) {
                    createChest(targetBlock);
                }

                player.closeInventory();
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
            }
        }
    }

    private void handleSelectCategoryMenuClick(InventoryClickEvent event, Player player, ItemStack currentItem) throws InstantiationException, IllegalAccessException {
        if (currentItem == null || currentItem.getType() == Material.AIR) return;

        List<Integer> slotsForCategory = Arrays.asList(10, 12, 14, 16, 37, 39, 41, 43);
        if (!slotsForCategory.contains(event.getSlot())) return;

        String displayName = ChatColor.stripColor(currentItem.getItemMeta().getDisplayName()).trim();
        CodingCategoryType categoryType = CodingCategoryType.getByDisplayName(displayName);
        if (categoryType != null) {
            player.openInventory(categoryType.categoryClass.newInstance().getInventory());
        }
    }

    private void handleWorldsMenuClick(InventoryClickEvent event, Player player, ItemStack currentItem) {
        if (event.getSlot() == 49) {
            MyWorlds mw = new MyWorlds("Мои миры");
            mw.setPlayer(player);
            player.openInventory(mw.getInventory());
        } else if (Arrays.asList(20, 21, 22, 23, 24, 29, 30, 31, 32, 33).contains(event.getSlot())) {
            if (currentItem != null) {
                List<String> lore = currentItem.getItemMeta().getLore();
                int id = Integer.parseInt(lore.get(lore.size() - 2).split(":")[1].trim().substring(2));
                Plot plot = Plot.getById(id);
                if (plot != null) {
                    player.closeInventory();
                    if (!plot.getPlotLoaded()) plot.load(plot.getPlotName());
                    Plot.teleportToPlot(plot, player);
                }
            }
        }
    }

    private void handleMyWorldsClick(InventoryClickEvent event, ItemStack currentItem) {
        if (currentItem == null) return;

        if (currentItem.getType() == Material.WHITE_STAINED_GLASS) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            new Plot(Plot.getNextPlotName(), event.getWhoClicked().getName(), PlotInitializeReason.PLAYER_PLOT_CREATED);
        } else {
            List<String> lore = currentItem.getItemMeta().getLore();
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

    private void createChest(Block targetBlock) {
        Block chest = targetBlock.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP);
        if (chest.getType() != Material.AIR) chest.setType(Material.AIR);
        chest.setType(Material.CHEST);

        File chestsFolder = new File(Bukkit.getWorldContainer() + File.separator + chest.getWorld().getName() + File.separator + "chests");
        File chestFile = new File(chestsFolder, chest.getLocation() + ".txt");
        if (chestFile.exists()) chestFile.delete();

        try {
            FileUtil.createNewFile(chestsFolder, chest.getLocation() + ".txt");
        } catch (IOException e) {
            Bukkit.getLogger().severe(e.getLocalizedMessage());
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
        Plot plot = Plot.getByWorld(player.getWorld());

        if (event.getAction().isRightClick() && target != null && target.getType() == Material.CHEST && player.getWorld().getName().endsWith("_dev")) {
            event.setCancelled(true);
            Chest chest = (Chest) target.getState();

            if (!Objects.equals(chest.getBlockInventory().getItem(0), new ItemStack(Material.BARRIER))) {
                String actionDisplayName = ((Sign) target.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH).getState()).getLine(2);
                player.openInventory(CodingHandleUtils.loadChestInventory(player.getWorld(), target.getLocation(), actionDisplayName, LoadInventoryReason.PLAYER_CHEST_OPEN));
                chest.getBlockInventory().setItem(0, new ItemStack(Material.BARRIER));
            } else {
                player.sendMessage("§bCreative+ §8» §fКакой-то игрок §6уже взаимодействует §fс этим сундуком.");
            }
        }

        if (event.getItem() != null && !Objects.equals(event.getItem(), new ItemStack(Material.AIR)) && event.getItem().getType() == Material.PAPER) {
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
        } else if (event.getItem() != null && !Objects.equals(event.getItem(), new ItemStack(Material.AIR)) && event.getItem().getType() == Material.APPLE) {
            player.openInventory(new GameValues("Игровое значение").getInventory());
        } else if (event.getItem() != null && !Objects.equals(event.getItem(), new ItemStack(Material.AIR)) && event.getItem().getType() == Material.MAGMA_CREAM) {
            if (player.isSneaking()) {
                ItemStack eventItem = event.getItem();
                ItemMeta meta = eventItem.getItemMeta();
                List<String> lore = meta.getLore();
                String isSaved = lore.get(0);
                if (ChatColor.stripColor(isSaved).equalsIgnoreCase("СОХРАНЕНО")) {
                    lore.remove(0);
                } else {
                    lore.add(0, "§dСОХРАНЕНО");
                }
                meta.setLore(lore);
                eventItem.setItemMeta(meta);
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
                case OAK_PLANKS -> player.openInventory(new ifPlayer("Если игрок").getInventory());
                case NETHER_BRICKS -> player.openInventory(new GameAction("Игровое действие").getInventory());
                case IRON_BLOCK -> player.openInventory(new ActionVar("Работа с переменными").getInventory());
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
                    case COBBLESTONE, NETHER_BRICKS, IRON_BLOCK -> {
                        b.getRelative(BlockFace.NORTH).setType(Material.AIR);
                        b.setType(Material.AIR);
                        b.getRelative(BlockFace.UP).setType(Material.AIR);
                        b.getRelative(BlockFace.WEST).setType(Material.AIR);

                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            Location lastStringBlock = getLastStringBlock(finalB.getLocation());
                            CodingHandleUtils.moveBlocks(finalB.getLocation().add(-2, 0, 0), lastStringBlock.add(-1, 1, -1), BlockFace.EAST, 2);
                        }, 5L);
                    }
                    case OAK_PLANKS -> {
                        Block endBlock = CodingHandleUtils.getLastPiston(b.getRelative(BlockFace.WEST));
                        b.getRelative(BlockFace.WEST).setType(Material.AIR);
                        b.getRelative(BlockFace.UP).setType(Material.AIR);
                        b.getRelative(BlockFace.NORTH).setType(Material.AIR);
                        if (endBlock != null) {
                            for(Block chestBlock = b.getRelative(BlockFace.UP); !chestBlock.getRelative(BlockFace.DOWN).getLocation().equals(endBlock.getLocation()); chestBlock = chestBlock.getRelative(BlockFace.WEST)) {
                                if (chestBlock.getType() == Material.CHEST) {
                                    chestBlock.setType(Material.AIR, true);
                                }
                            }

                            Location relative = b.getRelative(BlockFace.NORTH).getLocation();

                            CodingHandleUtils.setBlocks(BukkitAdapter.adapt(relative.getWorld()), BlockVector3.at(relative.getBlockX(), relative.getBlockY(), relative.getBlockZ()), BlockVector3.at(endBlock.getX(), endBlock.getY(), endBlock.getZ()));
                            CodingHandleUtils.moveBlocks(endBlock.getLocation().add(-1, 1, -1), getLastStringBlock(endBlock.getLocation()), BlockFace.EAST, (int) b.getLocation().distance(endBlock.getLocation()) + 1);
                        }
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
                    case OAK_PLANKS -> {
                        relativeBlock = Material.PISTON;
                        blockName = "Если игрок";
                    }
                    case NETHER_BRICKS -> {
                        relativeBlock = Material.NETHERRACK;
                        blockName = "Игровое действие";
                    }
                    case IRON_BLOCK -> {
                        relativeBlock = Material.IRON_ORE;
                        blockName = "Работа с переменными";
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
            Block relative = mainBlock.getBlock().getRelative(BlockFace.WEST);
            if (additional != Material.PISTON) {
                if (relative.getType() != Material.AIR) {
                    Location pos1 = relative.getLocation();
                    Location pos2 = getLastStringBlock(pos1).add(0, 1, -1);

                    CodingHandleUtils.moveBlocks(pos1, pos2, BlockFace.WEST, 2);
                }
                relative.setType(additional);
            } else {
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
                piston.setType(Material.PISTON);
                Directional facing1 = (Directional) piston.getBlockData();
                facing1.setFacing(BlockFace.EAST);
                piston.setBlockData(facing1);
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

        return b.getLocation().add(-1, 0, 0);
    }

    @EventHandler
    public void onPlayerChatted(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String message = LegacyComponentSerializer.legacySection().serialize(event.message());
        if (message.length() > 256) message = message.substring(0, 256);
        if (message.contains("&")) message = message.replace("&", "§");

        Plot plot = Plot.getByWorld(player.getWorld());

        if (plot != null && plot.getLinkedDevPlot().getWorld() == player.getWorld()) {
            ItemStack activeItem = player.getInventory().getItemInMainHand();
            ItemMeta meta = activeItem.getItemMeta();

            if (activeItem.getType() != Material.AIR) {

                switch (activeItem.getType()) {
                    case BOOK, MAGMA_CREAM -> {
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

                            String displayValue;
                            if (d == (long) d) {
                                displayValue = String.valueOf((long) d);
                            } else {
                                displayValue = String.valueOf(d);
                            }

                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                            player.sendTitle("§aЗначение установлено", displayValue, 10, 70, 20);

                            meta = activeItem.getItemMeta();
                            if (meta != null) {
                                meta.setDisplayName(displayValue);
                                activeItem.setItemMeta(meta);
                            }
                        } catch (NumberFormatException e) {
                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0F, 1.0F);
                            player.sendTitle("§cНекорректное значение", "§6" + message, 10, 70, 20);
                        }
                    }

                }

                activeItem.setItemMeta(meta);
                player.getInventory().setItemInMainHand(activeItem);
            }
        }
    }

}
