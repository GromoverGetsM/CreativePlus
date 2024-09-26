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
import ru.rstudios.creativeplus.utils.CodingHandleUtils;

import java.util.List;
import java.util.Locale;

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

        for (Entity entity : selection) {
            if (entity instanceof Player player) {
                NamespacedKey key = new NamespacedKey(plugin, event.getPlot().getPlotName() + "/" + entity.getName());

                String title = CodingHandleUtils.parseText(this.getTexts()[0]);
                String description = CodingHandleUtils.parseText(this.getTexts()[1]);
                Material item = this.getNonNullItems()[2] == null ? Material.DIAMOND : this.getNonNullItems()[2].getType();

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
                        + "  \"description\": \"" + description + "\","
                        + "  \"frame\": \"task\","
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

                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        Bukkit.getUnsafe().removeAdvancement(key);
                    }, 2L);
                }
            }
        }
    }

}
