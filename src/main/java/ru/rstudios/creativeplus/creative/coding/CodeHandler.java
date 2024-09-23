package ru.rstudios.creativeplus.creative.coding;

import com.jeff_media.jefflib.ItemStackSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.actions.ActionIf;
import ru.rstudios.creativeplus.creative.coding.actions.ActionType;
import ru.rstudios.creativeplus.creative.coding.dynamicvariables.DynamicVariable;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.creative.coding.starters.StarterType;
import ru.rstudios.creativeplus.creative.plots.DevPlot;
import ru.rstudios.creativeplus.creative.plots.Plot;
import ru.rstudios.creativeplus.utils.CodingHandleUtils;
import ru.rstudios.creativeplus.utils.LoadInventoryReason;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class CodeHandler {

    private Plot linked;
    private List<Starter> starters = new ArrayList<>();
    private HashMap<String, DynamicVariable> dynamicvariables = new LinkedHashMap<>();

    public CodeHandler (Plot plot) {
        this.linked = plot;
    }

    public void parseCodeBlocks() {
        Location startBlock = new Location(linked.getLinkedDevPlot().getWorld(), 60, -59, 60);
        if (!this.starters.isEmpty()) this.starters.clear();
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
                Location actionLoc = loc.clone().set(dx, loc.getBlockY(), loc.getBlockZ());
                Block actionBlock = actionLoc.getBlock();

                if (!DevPlot.getActionBlocks().contains(actionBlock.getType())) continue;

                Sign aS = (Sign) actionBlock.getRelative(BlockFace.NORTH).getState();
                String actionName = aS.getLine(2);

                Location chest = actionBlock.getRelative(BlockFace.UP).getLocation();
                Inventory i = null;

                if (chest.getBlock().getType() == Material.CHEST) {
                    i = CodingHandleUtils.loadChestInventory(chest.getWorld(), chest,
                            ((Sign) chest.getBlock().getRelative(BlockFace.DOWN)
                                    .getRelative(BlockFace.NORTH).getState()).getLine(2), LoadInventoryReason.CODE_PARSE);
                }

                Block westBlock = actionBlock.getRelative(BlockFace.WEST);
                if (actionBlock.getType() == Material.OAK_PLANKS &&
                        westBlock.getType() == Material.PISTON &&
                        ((Directional) westBlock.getBlockData()).getFacing() == BlockFace.WEST) {

                    Block closingPiston = CodingHandleUtils.getLastPiston(westBlock);
                    if (closingPiston != null) {
                        List<Action> conditionalActions = new ArrayList<>();
                        for (int insideDx = dx - 2; insideDx > closingPiston.getX(); insideDx -= 2) {
                            Location insideLoc = loc.clone().set(insideDx, loc.getBlockY(), loc.getBlockZ());
                            Block insideBlock = insideLoc.getBlock();
                            if (DevPlot.getActionBlocks().contains(insideBlock.getType())) {
                                Sign insideSign = (Sign) insideBlock.getRelative(BlockFace.NORTH).getState();
                                String insideActionName = insideSign.getLine(2);

                                Location insideChest = insideBlock.getRelative(BlockFace.UP).getLocation();
                                Inventory insideInventory = null;

                                if (insideChest.getBlock().getType() == Material.CHEST) {
                                    insideInventory = CodingHandleUtils.loadChestInventory(insideChest.getWorld(), insideChest,
                                            ((Sign) insideChest.getBlock().getRelative(BlockFace.DOWN)
                                                    .getRelative(BlockFace.NORTH).getState()).getLine(2), LoadInventoryReason.CODE_PARSE);
                                }

                                Action insideAct = null;
                                ActionType insideActionType = ActionType.getByCustomName(insideActionName);
                                if (insideActionType != null) insideAct = insideActionType.create(starter, insideInventory);
                                if (insideAct != null) conditionalActions.add(insideAct);
                            }
                        }

                        ActionIf condition = null;
                        ActionType condType = ActionType.getByCustomName(actionName);
                        if (condType != null) condition = condType.createCondition(starter, i, conditionalActions);
                        if (condition != null) actions.add(condition);

                        dx = closingPiston.getX();
                        continue;
                    }
                }


                Action act = null;
                ActionType actionType = ActionType.getByCustomName(actionName);
                if (actionType != null) act = actionType.create(starter, i);
                if (act != null) actions.add(act);
            }

            if (starter != null) {
                starter.setActions(actions);
                starters.add(starter);
            }
        }
        this.starters.addAll(starters);
        loadDynamicVariables();
    }

    private void loadDynamicVariables() {
        File varsFile = new File(Bukkit.getWorldContainer() + File.separator + linked.getLinkedDevPlot().getWorld().getName() + File.separator + "DynamicVariables.yml");

        if (varsFile.exists() && varsFile.isFile() && varsFile.length() != 0) {
            FileConfiguration vars = YamlConfiguration.loadConfiguration(varsFile);

            for (String key : vars.getKeys(false)) {
                ConfigurationSection section = vars.getConfigurationSection(key);

                if (section != null) {
                    String name = section.getString("name", "");
                    Object value = section.get("value", "");
                    String typeMarker = section.getString("type", "null");
                    boolean isSaved = section.getBoolean("saved", false);

                    if (typeMarker.equalsIgnoreCase("itemstack") && value instanceof String) {
                        String valueCache = value.toString();
                        value = ItemStackSerializer.fromBase64(valueCache);
                    }

                    if (!name.isEmpty()) this.dynamicvariables.put(name, new DynamicVariable(name, value, isSaved));
                }
            }
        }
    }

    public void saveDynamicVariables() {
        File varsFile = new File(Bukkit.getWorldContainer() + File.separator + linked.getLinkedDevPlot().getWorld().getName() + File.separator + "DynamicVariables.yml");

        if (varsFile.exists() && varsFile.isFile() && varsFile.length() != 0) {
            varsFile.delete();
            try {
                varsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe(e.getLocalizedMessage());
            }
        }

        FileConfiguration vars = YamlConfiguration.loadConfiguration(varsFile);

        System.out.println(this.dynamicvariables.keySet());
        for (String key : this.dynamicvariables.keySet()) {
            if (key == null || key.trim().isEmpty()) {
                plugin.getLogger().severe("Skipping dynamic variable with an empty or null key.");
                continue; // Пропустить пустой или null ключ
            }

            ConfigurationSection section = vars.createSection(key);

            DynamicVariable variable = this.dynamicvariables.get(key);
            String name = variable.getName();
            Object value = variable.getValue(linked);
            boolean isSaved = variable.isSaved();

            if (isSaved) {
                section.set("name", name);
                section.set("value", value);
                section.set("saved", true);

                if (value instanceof String) {
                    section.set("type", "text");
                } else if (value instanceof Number) {
                    section.set("type", "num");
                } else if (value instanceof Location) {
                    section.set("type", "loc");
                } else if (value instanceof ItemStack) {
                    section.set("type", "itemstack");
                    try {
                        section.set("value", ItemStackSerializer.toBase64((ItemStack) value));
                    } catch (IOException e) {
                        plugin.getLogger().severe(e.getLocalizedMessage());
                    }
                }
            }
        }


        try {
            vars.save(varsFile);
        } catch (IOException e) {
            plugin.getLogger().severe(e.getLocalizedMessage());
        }
    }

    public HashMap<String, DynamicVariable> getDynamicVariables() {
        return this.dynamicvariables;
    }



    public void sendStarter (GameEvent event) {
        if (this.starters != null && !this.starters.isEmpty()) {

            for (Starter starter : this.starters) {
                if (StarterType.getByCustomName(starter.getName()) != null && StarterType.getByCustomName(starter.getName()).getEventClass() == event.getClass()) {
                    starter.setSelection(Collections.singletonList(event.getDefaultEntity()));
                    starter.executeActions(event);
                }
            }

        }
    }

}
