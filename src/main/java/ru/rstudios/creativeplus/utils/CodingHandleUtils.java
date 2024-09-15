package ru.rstudios.creativeplus.utils;

import com.jeff_media.jefflib.ItemStackSerializer;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;
import ru.rstudios.creativeplus.creative.coding.actions.ActionType;
import ru.rstudios.creativeplus.creative.menus.coding.CreativeHolder;

import java.io.*;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class CodingHandleUtils {

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

    public static void saveInventoryToChest (org.bukkit.World world, Location chest, Inventory inventory) {
        File chestsFolder = new File(Bukkit.getWorldContainer(), world.getName() + File.separator + "chests");

        if (!chestsFolder.exists()) {
            chestsFolder.mkdirs();
        }

        File chestFile = new File(chestsFolder, chest.toString() + ".txt");

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

    public static Inventory loadChestInventory (org.bukkit.World world, Location chest, String actionDisplayName) {
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
                } else {
                    inv = ActionType.getByDisplayName(actionDisplayName).getmClass().newInstance().getInventory();
                }
            } catch (IOException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        } else {
            try {
                inv = ActionType.getByDisplayName(actionDisplayName).getmClass().newInstance().getInventory();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (inv != null) {
            Sign sign = (Sign) chest.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH).getState();
            String name = sign.getLine(2);
            Inventory newInv = null;
            try {
                newInv = Bukkit.createInventory(ActionType.getByDisplayName(actionDisplayName).getmClass().newInstance().getInventory().getHolder(), inv.getSize(), name);
            } catch (InstantiationException | IllegalAccessException e) {
                plugin.getLogger().severe(e.getLocalizedMessage());
            }

            newInv.setContents(inv.getContents());

            inv = newInv;
        }

        return inv;
    }

}
