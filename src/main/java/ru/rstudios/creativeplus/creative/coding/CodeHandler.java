package ru.rstudios.creativeplus.creative.coding;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.actions.ActionType;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.creative.coding.starters.StarterType;
import ru.rstudios.creativeplus.creative.menus.CreativeSystemMenu;
import ru.rstudios.creativeplus.creative.menus.coding.CreativeHolder;
import ru.rstudios.creativeplus.creative.plots.DevPlot;
import ru.rstudios.creativeplus.creative.plots.Plot;
import ru.rstudios.creativeplus.utils.InventoryUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CodeHandler {

    private Plot linked;
    private List<Starter> starters;

    public CodeHandler (Plot plot) {
        this.linked = plot;
    }

    public void parseCodeBlocks() {
        Location startBlock = new Location(linked.getLinkedDevPlot().getWorld(), 60, -59, 60);
        List<Starter> starters = new ArrayList<>();

        for (int dz = 60; dz > -60; dz -= 4) {
            Location loc = startBlock.clone().set(startBlock.getBlockX(), startBlock.getBlockY(), dz);
            if (!DevPlot.getStarterBlocks().contains(loc.getBlock().getType())) continue;

            Starter starter = null;

            Sign s = (Sign) loc.getBlock().getRelative(BlockFace.NORTH).getState();
            String starterName = s.getLine(2);

            List<Action> actions = new ArrayList<>();

            StarterType type = StarterType.getByCustomName(starterName);
            if (type != null) starter = type.create(actions);

            for (int dx = 58; dx > -60; dx -= 2) {
                Location action = loc.clone().set(dx, loc.getBlockY(), loc.getBlockZ());
                if (!DevPlot.getActionBlocks().contains(action.getBlock().getType())) continue;

                Sign aS = (Sign) action.getBlock().getRelative(BlockFace.NORTH).getState();
                String actionName = aS.getLine(2);
                List<Entity> selection = starter == null ? new ArrayList<>() : starter.getSelection();

                Location chest = action.getBlock().getRelative(BlockFace.UP).getLocation();

                Inventory i = null;

                if (chest.getBlock().getType() == Material.CHEST) {
                    File chestFile = new File(Bukkit.getWorldContainer() + File.separator + linked.getLinkedDevPlot().devPlotName + File.separator + "chests" + File.separator + chest + ".yml");
                    i = InventoryUtil.getInventory(YamlConfiguration.loadConfiguration(chestFile), new CreativeHolder());
                }
                
                Action act = null;
                ActionType actionType = ActionType.getByCustomName(actionName);
                if (actionType != null) act = actionType.create(starter, selection, i);
                if (act != null) actions.add(act);
            }

            if (starter != null) {
                starter.setActions(actions);
                starters.add(starter);
            }

        }

        System.out.println(starters);
        this.starters = starters;
    }

    public void sendStarter (GameEvent event) {
        if (this.starters != null && !this.starters.isEmpty()) {

            for (Starter starter : this.starters) {
                if (StarterType.getByCustomName(starter.getName()) != null && StarterType.getByCustomName(starter.getName()).getEventClass() == event.getClass()) {
                    starter.executeActions();
                }
            }

        }
    }

}
