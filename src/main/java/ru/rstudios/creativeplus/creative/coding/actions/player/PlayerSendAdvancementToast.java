package ru.rstudios.creativeplus.creative.coding.actions.player;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.actions.ActionType;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.creative.menus.coding.SwitchItem;
import ru.rstudios.creativeplus.utils.CodingHandleUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static ru.rstudios.creativeplus.CreativePlus.plugin;

public class PlayerSendAdvancementToast extends Action {

    private final Inventory inventory;
    private Starter starter;

    public PlayerSendAdvancementToast (Starter starter, String name, Inventory inventory) {
        super(starter, name, inventory);
        this.inventory = inventory;
        this.starter = starter;
    }

    @Override
    public ItemStack getIcon() {
        return null;
    }

    @Override
    public String getName() {
        return "Отправить достижение";
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public ActionType getType() {
        return ActionType.SEND_MESSAGE;
    }

    @Override
    public void execute (GameEvent event) {
        List<Entity> selection = starter.getSelection();

        this.initInventorySort();

        for (Entity entity : selection) {
            if (entity instanceof Player player) {
                NamespacedKey key = new NamespacedKey(plugin, event.getPlot().getPlotName() + "/" + entity.getName() + "/" + UUID.randomUUID());

                String title = CodingHandleUtils.parseText(this.getTexts()[0]);
                Material item = this.getNonNullItems()[1] == null ? Material.DIAMOND : this.getNonNullItems()[1].getType();
                String frame = "task";

                SwitchItem switchItem = SwitchItem.getByConfigName("SendAdvancementToast#15");

                if (switchItem != null) {
                    switch (switchItem.getStateName(switchItem.getCurrentState(this.getNonNullItems()[this.getNonNullItems().length - 1]))) {
                        case "Цель" -> frame = "goal";
                        case "Челлендж" -> frame = "challenge";
                    }
                }

                String advancementJSON = "{"
                        + "\"criteria\": {"
                        + "  \"impossible\": {"
                        + "    \"trigger\": \"minecraft:impossible\""
                        + "  }"
                        + "},"
                        + "\"display\": {"
                        + "  \"icon\": {"
                        + "    \"item\": \"" + "minecraft:" + item.name().toLowerCase(Locale.ROOT) + "\""
                        + "  },"
                        + "  \"title\": \"" + title + "\","
                        + "  \"description\": \"" + "Unknown" + "\","
                        + "  \"frame\": \"" + frame + "\","
                        + "  \"announce_to_chat\": false,"
                        + "  \"show_toast\": true,"
                        + "  \"hidden\": true"
                        + "}"
                        + "}";

                Bukkit.getUnsafe().removeAdvancement(key);
                Advancement advancement = Bukkit.getUnsafe().loadAdvancement(key, advancementJSON);

                if (advancement != null) {
                    AdvancementProgress progress = player.getAdvancementProgress(advancement);

                    progress.awardCriteria("impossible");

                    Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.getUnsafe().removeAdvancement(key), 2L);
                }
            }
        }
    }

}
