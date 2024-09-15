package ru.rstudios.creativeplus.creative.menus.coding.starters;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.rstudios.creativeplus.creative.coding.starters.StarterType;
import ru.rstudios.creativeplus.creative.menus.CreativeSystemMenu;
import ru.rstudios.creativeplus.creative.menus.main.MyWorlds;
import ru.rstudios.creativeplus.utils.InventoryUtil;

import java.io.File;
import java.util.HashMap;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class PlayerEvent extends CreativeSystemMenu implements Listener {

    private final String name;
    private final int rows;
    private final HashMap<Integer, ItemStack> items;
    private Player player;

    public PlayerEvent (String name) {
        this(name, 1);
    }

    public PlayerEvent (String name, int rows) {
        this(name, rows, null);
    }

    public PlayerEvent (String name, int rows, HashMap<Integer, ItemStack> items) {
        super(name, rows, items);
        this.name = name;
        this.rows = rows;
        this.items = items;

        registerEvents();
    }

    public void setPlayer (Player player) {
        this.player = player;
    }


    @Override
    public @NotNull Inventory getInventory() {
        File inventory = new File(plugin.getDataFolder() + File.separator + "menus" + File.separator + "coding" + File.separator + "starters" + File.separator + this.getClass().getSimpleName() + ".yml");
        Inventory i;

        if (!inventory.exists() || !inventory.isFile() || inventory.length() == 0) {
            i = Bukkit.createInventory(this, this.rows * 9, this.name);

            for (Integer key : this.items.keySet()) {
                if (key <= this.rows * 9) i.setItem(key, this.items.get(key));
                else {
                    plugin.getLogger().warning("Установка предмета невозможна. Слот " + key + " в инвентаре " + this.name + " превышает допустимое значение.");
                    break;
                }
            }
        } else {
            i = InventoryUtil.getInventory(YamlConfiguration.loadConfiguration(inventory), this);
        }

        return i;
    }

    @Override
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick (InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() != null && event.getClickedInventory().getHolder() instanceof PlayerEvent) {
            if (event.getSlot() == 10) {
                event.getWhoClicked().openInventory(new PlayerEvent.WorldInteractCategory("Событие игрока").getInventory());
            }
        }
    }

    @Override
    public void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static class WorldInteractCategory extends CreativeSystemMenu implements Listener {

        private final String name;
        private final int rows;
        private final HashMap<Integer, ItemStack> items;
        private Player player;

        public WorldInteractCategory (String name) {
            this(name, 1);
        }

        public WorldInteractCategory (String name, int rows) {
            this(name, rows, null);
        }

        public WorldInteractCategory (String name, int rows, HashMap<Integer, ItemStack> items) {
            super(name, rows, items);
            this.name = name;
            this.rows = rows;
            this.items = items;

            registerEvents();
        }

        public void setPlayer (Player player) {
            this.player = player;
        }


        @Override
        public @NotNull Inventory getInventory() {
            File inventory = new File(plugin.getDataFolder() + File.separator + "menus" + File.separator + "coding" + File.separator + "starters" + File.separator + this.getClass().getSimpleName() + ".yml");
            Inventory i;

            if (!inventory.exists() || !inventory.isFile() || inventory.length() == 0) {
                i = Bukkit.createInventory(this, this.rows * 9, this.name);

                for (Integer key : this.items.keySet()) {
                    if (key <= this.rows * 9) i.setItem(key, this.items.get(key));
                    else {
                        plugin.getLogger().warning("Установка предмета невозможна. Слот " + key + " в инвентаре " + this.name + " превышает допустимое значение.");
                        break;
                    }
                }
            } else {
                i = InventoryUtil.getInventory(YamlConfiguration.loadConfiguration(inventory), this);
            }

            return i;
        }

        @Override
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onClick (InventoryClickEvent event) {
            Player player = (Player) event.getWhoClicked();
            if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() != null && event.getClickedInventory().getHolder() instanceof WorldInteractCategory) {
                event.setCancelled(true);
                Block targetBlock = player.getTargetBlockExact(5);
                if (event.getCurrentItem() != null && targetBlock != null && targetBlock.getType() == Material.OAK_WALL_SIGN) {
                    Sign sign = (Sign) targetBlock.getState();
                    String itemDisplayName = event.getCurrentItem().getItemMeta().getDisplayName();
                    sign.setLine(2, StarterType.getByDisplayName(ChatColor.stripColor(itemDisplayName).trim()).getName());
                    sign.update();
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                }
            }
        }

        @Override
        public void registerEvents() {
            Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

}
