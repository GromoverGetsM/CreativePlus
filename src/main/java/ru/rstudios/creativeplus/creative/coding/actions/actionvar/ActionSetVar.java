package ru.rstudios.creativeplus.creative.coding.actions.actionvar;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.actions.ActionType;
import ru.rstudios.creativeplus.creative.coding.dynamicvariables.DynamicVariable;
import ru.rstudios.creativeplus.creative.coding.events.GameEvent;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.utils.CodingHandleUtils;

import java.util.Arrays;

public class ActionSetVar extends Action {

    private Starter starter;
    private Inventory inventory;
    public ActionSetVar(Starter starter, String name, Inventory inventory) {
        super(starter, name, inventory);
        this.starter = starter;
        this.inventory = inventory;
    }

    @Override
    public ItemStack getIcon() {
        return null;
    }

    @Override
    public String getName() {
        return "Установить (=)";
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public ActionType getType() {
        return ActionType.CANCEL_EVENT;
    }

    @Override
    public void execute(GameEvent event) {
        this.initInventorySort();
        ItemStack dynamic = this.getDynamicVariables()[0];
        ItemStack[] nonnull = Arrays.copyOf(this.getNonNullItems(), this.getNonNullItems().length);
        nonnull = ArrayUtils.remove(nonnull, 0);

        if (dynamic.getItemMeta().hasDisplayName()) {

            for (Entity entity : this.starter.getSelection()) {
                DynamicVariable variable = new DynamicVariable(this.replacePlaceholders(ChatColor.stripColor(dynamic.getItemMeta().getDisplayName()), event, entity));

                if (nonnull.length == 1) {
                    variable.setValue(event.getPlot(), CodingHandleUtils.parseItem(nonnull[0], event, entity, this.starter));
                } else if (nonnull.length == 0) {
                    variable.setValue(event.getPlot(), "");
                } else {
                    StringBuilder builder = new StringBuilder();

                    for (ItemStack item : nonnull) {
                        builder.append(CodingHandleUtils.parseItem(item, event, entity, this.starter));
                    }

                    String result = builder.toString();
                    if (result.length() > 1024) result = result.substring(0, 1024);

                    variable.setValue(event.getPlot(), result);
                }
            }
        }
    }

}
