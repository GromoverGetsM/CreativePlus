package ru.rstudios.creativeplus.creative.coding.actions.player;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
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

import java.util.List;
import java.util.Locale;

public class PlayerPlaySound extends Action {

    private final Inventory inventory;
    private Starter starter;

    public PlayerPlaySound (Starter starter, String name, Inventory inventory) {
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
        return "Проиграть звук";
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public ActionType getType() {
        return ActionType.PLAY_SOUND;
    }

    @Override
    public void execute(GameEvent event) {
        List<Entity> selection = starter.getSelection();
        this.initInventorySort();

        SwitchItem item = SwitchItem.getByConfigName("PlaySound#17");
        SoundCategory category = SoundCategory.MASTER;

        if (item != null) {
            switch (item.getStateName(item.getCurrentState(this.getNonNullItems()[this.getNonNullItems().length - 1]))) {
                case "Музыка" -> category = SoundCategory.MUSIC;
                case "Погода" -> category = SoundCategory.WEATHER;
                case "Враждебные существа" -> category = SoundCategory.HOSTILE;
                case "Игроки" -> category = SoundCategory.PLAYERS;
                case "Голос/Речь" -> category = SoundCategory.VOICE;
                case "Музыкальные блоки" -> category = SoundCategory.RECORDS;
                case "Блоки" -> category = SoundCategory.BLOCKS;
                case "Мирные существа" -> category = SoundCategory.NEUTRAL;
                case "Окружение" -> category = SoundCategory.AMBIENT;
            }
        }

        for (Entity entity : selection) {
            if (entity instanceof Player player) {
                Location loc = (Location) CodingHandleUtils.parseItem(this.getLocations()[0], event, entity, this.starter);
                Sound sound = Sound.valueOf(CodingHandleUtils.parseText(this.getTexts()[0]).toUpperCase(Locale.ROOT));
                float volume = (float) Math.min(2.0, Math.max(0.5, (Double) CodingHandleUtils.parseItem(this.getNumbers()[0], event, entity, this.starter)));
                float pitch = (float) Math.min(2.0, Math.max(0.5, (Double) CodingHandleUtils.parseItem(this.getNumbers()[1], event, entity, this.starter)));

                player.playSound(loc, sound, category, volume, pitch);
            }
        }
    }

}
