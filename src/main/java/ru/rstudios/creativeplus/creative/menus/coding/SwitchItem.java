package ru.rstudios.creativeplus.creative.menus.coding;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class SwitchItem {
    private List<String> states;
    private List<Material> icons;
    private String name;
    private List<String> description;
    private int currentState;

    public SwitchItem(String name, List<String> description, List<String> states, List<Material> icons) {
        this.name = name;
        this.description = description;
        this.states = states;
        this.icons = icons;
        this.currentState = 0;
    }

    public void nextState() {
        currentState = (currentState + 1) % states.size();
    }

    public void previousState() {
        currentState = (currentState - 1 + states.size()) % states.size();
    }

    public ItemStack getCurrentIcon() {
        Material material = icons.get(currentState);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§3" + name);

            List<String> lore = new ArrayList<>();
            lore.addAll(description);
            lore.add("");
            lore.addAll(generateStateLore());

            meta.setLore(lore);

            PersistentDataContainer data = meta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(plugin, "switch_item");
            data.set(key, PersistentDataType.INTEGER, currentState);
            item.setItemMeta(meta);
        }
        return item;
    }

    // Метод для генерации отображения состояний
    private List<String> generateStateLore() {
        List<String> stateLore = new ArrayList<>();
        int totalStates = this.states.size();

        // Определяем индексы для предыдущих и следующих состояний
        int startIndex = Math.max(0, this.currentState - 2);
        int endIndex = Math.min(totalStates, this.currentState + 3);

        // Если есть состояния перед видимой зоной, добавляем первое состояние и "..."
        if (startIndex > 0) {
            stateLore.add("§7 ○ " + this.states.get(0));
            stateLore.add("§8 (...)");
        }

        for (int i = startIndex; i < endIndex; i++) {
            if (i == this.currentState) {
                stateLore.add("§4 ● " + this.states.get(i));
            } else {
                stateLore.add("§7 ○ " + this.states.get(i));
            }
        }

        // Если есть состояния после видимой зоны, добавляем "..." и последнее состояние
        if (endIndex < totalStates) {
            stateLore.add("§8 (...)");
            stateLore.add("§7 ○ " + this.states.get(totalStates - 1));
        }

        return stateLore;
    }


    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public static SwitchItem getByConfigName (String s) {
        File file = new File(plugin.getDataFolder() + File.separator + "menus" + File.separator + "coding" + File.separator + "switchitems" + File.separator + s + ".yml");

        if (file.exists() && file.isFile()) {
            FileConfiguration fc = YamlConfiguration.loadConfiguration(file);

            String name = fc.getString("name", "");
            List<String> lore = fc.getStringList("lore");
            List<String> states = fc.getStringList("states");
            List<Material> icons = fc.getStringList("icons").stream()
                    .map(String::toUpperCase)
                    .map(Material::valueOf)
                    .toList();
            return new SwitchItem(name, lore, states, icons);
        }
        return null;
    }

    public int getCurrentState (ItemStack item) {
        return this.icons.indexOf(item.getType());
    }

    public String getStateName (int currentState) {
        return this.states.get(currentState);
    }
    public List<String> getStates() {
        return this.states;
    }
}
