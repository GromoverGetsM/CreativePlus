package ru.rstudios.creativeplus.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import ru.rstudios.creativeplus.utils.UniversalMenuHolder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class CreateInventory implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 2) {
            Inventory inventory = Bukkit.createInventory(new UniversalMenuHolder(), Integer.parseInt(args[0]), args[1]);
            if (sender instanceof Player player) {
                player.openInventory(inventory);
            }
        }
        return true;
    }

    @EventHandler
    public void onInventoryClosed (InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof UniversalMenuHolder) {
            invToYML(event.getInventory(), "userInventory" + event.getPlayer().getName(), event.getView().getTitle());
            event.getPlayer().sendMessage("Инвентарь сохранён в файл " + event.getInventory().getHolder() + ".yml");
        }
    }

    public static void invToYML(@NotNull Inventory inventory, @NotNull String configName, String inventoryTitle) {
        FileConfiguration config = new YamlConfiguration();
        String pathPrefix = "creative.menus." + configName;

        config.set(pathPrefix + ".size", inventory.getSize());
        config.set(pathPrefix + ".title", inventoryTitle);

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                String itemPath = pathPrefix + ".items.slot" + i;

                config.set(itemPath + ".type", item.getType().toString());
                config.set(itemPath + ".amount", item.getAmount());

                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    if (meta.hasDisplayName()) {
                        config.set(itemPath + ".name", meta.getDisplayName());
                    } else {
                        config.set(itemPath + ".name", item.getI18NDisplayName());
                    }

                    if (meta.hasLore()) {
                        config.set(itemPath + ".lore", meta.getLore());
                    }

                    if (meta.hasEnchants()) {
                        List<String> enchants = new ArrayList<>();
                        for (Map.Entry<Enchantment, Integer> enchant : meta.getEnchants().entrySet()) {
                            enchants.add(enchant.getKey().getKey().getKey() + ":" + enchant.getValue());
                        }
                        config.set(itemPath + ".enchantments", enchants);
                    }

                    if (meta instanceof SkullMeta skullMeta) {
                        try {
                            Field profileField = skullMeta.getClass().getDeclaredField("profile");
                            profileField.setAccessible(true);
                            GameProfile profile = (GameProfile) profileField.get(skullMeta);
                            if (profile != null && profile.getProperties().containsKey("textures")) {
                                Property textureProperty = profile.getProperties().get("textures").iterator().next();
                                if (textureProperty != null) {
                                    String texture = textureProperty.toString().split(",")[1].trim().substring(6);
                                    config.set(itemPath + ".texture", texture);
                                }
                            }
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }

                }

                config.set(itemPath + ".slot", i);
            }
        }

        try {
            config.save(new File(plugin.getDataFolder(), inventory.getHolder() + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}