package ru.rstudios.creativeplus.creative.coding.events;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public interface BlockEvent {

    Block getBlock();
    BlockFace getBlockFace();

}
