package ru.rstudios.creativeplus.utils;

import com.google.common.base.Preconditions;
import com.jeff_media.jefflib.ItemStackSerializer;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;
import ru.rstudios.creativeplus.creative.coding.actions.ActionType;
import ru.rstudios.creativeplus.creative.coding.dynamicvariables.DynamicVariable;
import ru.rstudios.creativeplus.creative.coding.events.*;
import ru.rstudios.creativeplus.creative.coding.eventvalues.ValueType;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.creative.menus.coding.CodingSystemMenu;
import ru.rstudios.creativeplus.creative.plots.Plot;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class CodingHandleUtils {

    public static final Pattern NUMBER = Pattern.compile("-?[0-9]+\\.?[0-9]*");

    public static void moveBlocks (Location pos1, Location pos2, BlockFace direction, int distance) {
        moveBlocks(pos1, pos2, direction, distance, EditSession::close);
    }

    public static void moveBlocks (Location pos1, Location pos2, BlockFace direction, int distance, Consumer<EditSession> consumer) {
        moveBlocks(pos1, pos2, direction, distance, consumer, null);
    }


    public static void moveBlocks (Location pos1, Location pos2, BlockFace direction, int distance, Consumer<EditSession> consumer, Executor executor) {
        Runnable runnable = () -> {
          World world = BukkitAdapter.adapt(pos1.getWorld());
          EditSession session = WorldEdit.getInstance().newEditSession(world);
          CuboidRegion region = new CuboidRegion(world, BlockVector3.at(pos1.getBlockX(), pos1.getBlockY(), pos1.getBlockZ()), BlockVector3.at(pos2.getBlockX(), pos2.getBlockY(), pos2.getBlockZ()));
            try {
                session.moveRegion(region, BlockVector3.at(direction.getModX(), direction.getModY(), direction.getModZ()), distance, true, null);
            } catch (MaxChangedBlocksException e) {
                plugin.getLogger().severe(e.getLocalizedMessage());
            }
            consumer.accept(session);
        };
        if (executor != null) {
            executor.execute(runnable);
        } else {
            runnable.run();
        }
    }


    public static void setBlocks (World world, BlockVector3 pos1, BlockVector3 pos2) {
        try (EditSession session = WorldEdit.getInstance().newEditSession(world)) {
            Region region = new CuboidRegion(pos1, pos2);
            session.setBlocks(region, BlockTypes.AIR.getDefaultState());
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }

    public static Block getLastPiston(Block startPiston) {
        Plot plot = Plot.getByWorld(startPiston.getWorld());

        if (plot != null) {
            int openPistons = 1;
            Block block = startPiston;

            while (plot.getLinkedDevPlot().inTerritory(block.getLocation())) {
                block = block.getRelative(BlockFace.WEST, 2);

                if (block.getType() == Material.PISTON) {
                    Directional pistonData = (Directional) block.getBlockData();
                    BlockFace pistonFacing = pistonData.getFacing();

                    if (pistonFacing == BlockFace.WEST) {
                        openPistons++;
                    } else if (pistonFacing == BlockFace.EAST) {
                        openPistons--;
                    }

                    if (openPistons == 0) {
                        return block;
                    }
                }
            }
        }

        return null;
    }


    public static void saveInventoryToChest (org.bukkit.World world, Location chest, Inventory inventory) {
        File chestsFolder = new File(Bukkit.getWorldContainer(), world.getName() + File.separator + "chests");

        if (!chestsFolder.exists()) {
            chestsFolder.mkdirs();
        }

        File chestFile = new File(chestsFolder, chest.toString() + ".txt");

        if (inventory.getHolder() instanceof CodingSystemMenu) {
            List<Integer> forRemoval = ((CodingSystemMenu) inventory.getHolder()).getDisallowedSlots();

            for (int i : forRemoval) {
                inventory.setItem(i, new ItemStack(Material.AIR));
            }

            String chestInventoryString = ItemStackSerializer.toBase64(inventory);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(chestFile))) {
                writer.write(chestInventoryString);
            } catch (IOException e) {
                plugin.getLogger().severe(e.getLocalizedMessage());
            }

            Block block = chest.getBlock();
            if (block.getType() == Material.CHEST) {
                Chest ch = (Chest) block.getState();
                Inventory chestInventory = ch.getBlockInventory();

                if (chestInventory.getItem(0) != null && chestInventory.getItem(0).getType() == Material.BARRIER) {
                    chestInventory.clear();
                }
            }
        }
    }

    public static Inventory loadChestInventory (org.bukkit.World world, Location chest, String actionDisplayName, LoadInventoryReason reason) {
        File chestsFolder = new File(Bukkit.getWorldContainer(), world.getName() + File.separator + "chests");
        File chestFile = new File(chestsFolder, chest.toString() + ".txt");

        Inventory inv = null;

        if (chestFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(chestFile))) {
                StringBuilder inventoryBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    inventoryBuilder.append(line);
                }
                String inventory = inventoryBuilder.toString();
                if (!inventory.isEmpty()) {
                    inv = ItemStackSerializer.inventoryFromBase64(inventory);
                }
                if (inv != null && reason == LoadInventoryReason.PLAYER_CHEST_OPEN) {
                    Inventory cacheInv = ActionType.getByCustomName(actionDisplayName).getmClass().newInstance().getInventory();
                    for (int i = 0; i < cacheInv.getSize(); i++) {
                        ItemStack item = cacheInv.getItem(i);
                        if (item != null && item.getType() != Material.AIR) {
                            inv.setItem(i, item);
                        }
                    }
                } else if (inv == null && reason == LoadInventoryReason.PLAYER_CHEST_OPEN) {
                    inv = ActionType.getByCustomName(actionDisplayName).getmClass().newInstance().getInventory();
                }
            } catch (IOException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        } else {
            try {
                inv = ActionType.getByCustomName(actionDisplayName).getmClass().newInstance().getInventory();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (inv != null) {
            Sign sign = (Sign) chest.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH).getState();
            String name = sign.getLine(2);
            Inventory newInv = null;
            try {
                newInv = Bukkit.createInventory(ActionType.getByCustomName(actionDisplayName).getmClass().newInstance().getInventory().getHolder(), inv.getSize(), name);
            } catch (InstantiationException | IllegalAccessException e) {
                plugin.getLogger().severe(e.getLocalizedMessage());
            }

            newInv.setContents(inv.getContents());

            inv = newInv;
        }

        return inv;
    }

    public static Object parseItem (ItemStack item, @Nullable GameEvent event, @Nullable Entity entity, @Nullable Starter starter) {
        if (item == null) {
            return null;
        }

        switch (item.getType()) {
            case BOOK -> {
                return parseText(item, event, entity);
            }
            case SLIME_BALL -> {
                return parseNumber(item);
            }
            case PAPER -> {
                return parseLocation(item, null, event);
            }
            case APPLE -> {
                return parseGameValue(item, event);
            }
            case MAGMA_CREAM -> {
                return parseDynamicVariable(item, "", true, event, starter);
            }
        }
        return null;
    }

    public static String parseText (ItemStack item, GameEvent event, Entity entity) {
        return parseText(item, "", event, entity);
    }

    public static String parseText (ItemStack item, String defaultText, GameEvent event, Entity entity) {
        return parseText(item, defaultText, true, event, entity);
    }

    public static String parseText (ItemStack item, String defaultText, boolean checkTypeMatches, GameEvent event, Entity entity) {
        if (item == null) {
            return defaultText;
        }
        if (checkTypeMatches && item.getType() != Material.BOOK) {
            return defaultText;
        } else {
            if (item.getItemMeta().hasDisplayName()) {
                String text = item.getItemMeta().getDisplayName();
                if (text.length() > 256) {
                    text = text.substring(0, 1024);
                }
                return text;
            } else {
                return defaultText;
            }
        }
    }

    public static Location parseLocation (ItemStack item, Location def, GameEvent event) {
        return parseLocation(item, def, true, event);
    }

    public static Location parseLocation (ItemStack item, Location def, boolean checkTypeMatches, GameEvent event) {
        Preconditions.checkNotNull(item);

        if (checkTypeMatches && item.getType() != Material.PAPER) {
            return def;
        } else {
            if (item.getItemMeta().hasDisplayName()) {
                String locString = ChatColor.stripColor(item.getItemMeta().getDisplayName()).trim();

                Pattern pattern = Pattern.compile("Location\\{world=CraftWorld\\{name=([^}]*)},x=([^,]*),y=([^,]*),z=([^,]*),pitch=([^,]*),yaw=([^}]*)}");
                Matcher matcher = pattern.matcher(locString);

                if (!matcher.matches()) {
                    return null;
                }

                String worldName = matcher.group(1);
                org.bukkit.World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    return null;
                }
                if (event.getPlot().getPlotWorld() != world) world = event.getPlot().getPlotWorld();

                double x = Double.parseDouble(matcher.group(2));
                double y = Double.parseDouble(matcher.group(3));
                double z = Double.parseDouble(matcher.group(4));
                float pitch = Float.parseFloat(matcher.group(5));
                float yaw = Float.parseFloat(matcher.group(6));

                return new Location(world, x, y, z, yaw, pitch);
            } else {
                return def;
            }
        }
    }

    public static double parseNumber (ItemStack item) {
        return parseNumber(item, 0.0);
    }

    public static double parseNumber (ItemStack item, double defaultNum) {
        return parseNumber(item, defaultNum, true);
    }

    public static double parseNumber (ItemStack item, double defaultNum, boolean checkTypeMatches) {
        if (item == null) {
            return defaultNum;
        }

        if (checkTypeMatches && item.getType() != Material.SLIME_BALL) {
            return defaultNum;
        } else {
            if (item.getItemMeta().hasDisplayName()) {
                String num = ChatColor.stripColor(item.getItemMeta().getDisplayName()).trim();
                return NUMBER.matcher(num).matches() ? Double.parseDouble(num) : defaultNum;
            } else {
                return defaultNum;
            }
        }
    }

    public static Object parseGameValue (ItemStack item) {
        return parseGameValue(item, null);
    }
    public static Object parseGameValue (ItemStack item, Object defaultValue) {
        return parseGameValue(item, defaultValue, true);
    }
    public static Object parseGameValue (ItemStack item, Object defaultValue, boolean checkTypeMatches) {
        if (item == null) {
            return defaultValue;
        }

        if (checkTypeMatches && item.getType() != Material.APPLE) {
            return defaultValue;
        } else {
            if (item.getItemMeta().hasDisplayName()) {
                String message = ChatColor.stripColor(item.getItemMeta().getDisplayName()).trim();
                try {
                    return ValueType.getByMessage(message) == null ? defaultValue : ValueType.getByMessage(message).getClazz().newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    plugin.getLogger().severe(e.getLocalizedMessage());
                }
            } else {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static Object parseDynamicVariable (ItemStack item, Object defaultValue, boolean checkTypeMatches, GameEvent event, Starter starter) {
        if (item == null) {
            return defaultValue;
        }

        if (checkTypeMatches && item.getType() != Material.MAGMA_CREAM) {
            return defaultValue;
        } else {
            if (item.getItemMeta().hasDisplayName()) {
                String displayName = replacePlaceholders(item.getItemMeta().getDisplayName(), event, starter);
                DynamicVariable variable = event.getPlot().getHandler().getDynamicVariables().get(displayName);
                return variable == null ? "" : variable.getValue(event.getPlot());
            } else {
                return defaultValue;
            }
        }
    }

    private static String replacePlaceholders (String s, GameEvent event, Starter starter) {
        if (s == null || s.isEmpty()) {
            return null;
        } else {
            s = StringUtils.replace(s, "%selected%", starter.getSelection().stream().map(Entity::getName).collect(Collectors.joining("")));
            s = StringUtils.replace(s, "%default%", event.getDefaultEntity().getName());
            s = StringUtils.replace(s, "%player%", event instanceof GamePlayerEvent ? ((GamePlayerEvent) event).getPlayer().getName() : "");
            s = StringUtils.replace(s, "%victim%", event instanceof DamageEvent ? ((DamageEvent) event).getVictim().getName() : event instanceof KillEvent ? ((KillEvent) event).getVictim().getName() : "");
            s = StringUtils.replace(s, "%damager%", event instanceof DamageEvent ? ((DamageEvent) event).getDamager().getName() : "");
            s = StringUtils.replace(s, "%killer%", event instanceof KillEvent ? ((KillEvent) event).getKiller().getName() : "");
            s = StringUtils.replace(s, "%shooter%", event instanceof DamageEvent ? ((DamageEvent) event).getShooter().getName() : "");
            s = StringUtils.replace(s, "%entity%", event instanceof EntityEvent ? ((EntityEvent) event).getEntity().getName() : "");
            return s;
        }
    }

}
