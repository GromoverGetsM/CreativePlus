package ru.rstudios.creativeplus.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockTypes;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class CodingHandleUtils {


    public static void moveBlocks (World world, BlockVector3 pos1, BlockVector3 pos2, BlockVector3 direction, int distance) {
        try (EditSession session = WorldEdit.getInstance().newEditSession(world)) {
            CuboidRegion region = new CuboidRegion(pos1, pos2);
            session.moveCuboidRegion(region, direction, distance, true, null);
            Operations.complete(session.commit());
        } catch (WorldEditException e) {
            plugin.getLogger().severe(e.getLocalizedMessage());
        }
    }

    public static void setBlocks (World world, BlockVector3 pos1, BlockVector3 pos2) {
        try (EditSession session = WorldEdit.getInstance().newEditSession(world)) {
            Region region = new CuboidRegion(pos1, pos2);
            session.setBlocks(region, BlockTypes.AIR.getDefaultState());
        } catch (MaxChangedBlocksException e) {
            plugin.getLogger().severe(e.getLocalizedMessage());
        }
    }

}
