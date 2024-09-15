package ru.rstudios.creativeplus.creative.menus.coding.actions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.rstudios.creativeplus.creative.menus.CreativeSystemMenu;
import ru.rstudios.creativeplus.utils.CodingHandleUtils;
import ru.rstudios.creativeplus.utils.InventoryUtil;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class SendMessage extends CreativeSystemMenu implements Listener {

    private final String name;
    private final int rows;
    private final HashMap<Integer, ItemStack> items;
    private Player player;

    public SendMessage (String name) {
        this(name, 1);
    }

    public SendMessage (String name, int rows) {
        this(name, rows, null);
    }

    public SendMessage (String name, int rows, HashMap<Integer, ItemStack> items) {
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
        File inventory = new File(plugin.getDataFolder() + File.separator + "menus" + File.separator + "coding" + File.separator + "actions" + File.separator + "playeraction" + File.separator + this.getClass().getSimpleName() + ".yml");
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
        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() != null && event.getClickedInventory().getHolder() instanceof SendMessage) {
            if (Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8).contains(event.getSlot()) || Arrays.asList(44, 43, 42, 41, 40, 39, 38, 37, 36).contains(event.getSlot())) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClose (InventoryCloseEvent event) {
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof SendMessage) {
            Player player = (Player) event.getPlayer();
            Block chest = player.getTargetBlockExact(5);

            if (chest != null && chest.getType() == Material.CHEST) {
                CodingHandleUtils.saveInventoryToChest(player.getWorld(), chest.getLocation(), event.getInventory());
            }
        }
    }

}
