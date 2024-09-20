package ru.rstudios.creativeplus.creative.coding.actions;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.rstudios.creativeplus.creative.coding.actions.ifplayer.PlayerNameEquals;
import ru.rstudios.creativeplus.creative.coding.actions.player.PlayerGiveItem;
import ru.rstudios.creativeplus.creative.coding.actions.player.PlayerSendMessage;
import ru.rstudios.creativeplus.creative.coding.actions.player.PlayerSendTitle;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.creative.menus.CreativeSystemMenu;
import ru.rstudios.creativeplus.creative.menus.coding.actions.ifplayer.NameEquals;
import ru.rstudios.creativeplus.creative.menus.coding.actions.playeraction.GiveItems;
import ru.rstudios.creativeplus.creative.menus.coding.actions.playeraction.SendMessage;
import ru.rstudios.creativeplus.creative.menus.coding.actions.playeraction.SendTitle;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ActionType {

    SEND_MESSAGE("Отправить сообщение", "Отправить сообщение", PlayerSendMessage.class, SendMessage.class, true),
    GIVE_ITEMS("Выдать предметы", "Выдать предметы", PlayerGiveItem.class, GiveItems.class, true),
    SEND_TITLE("Отправить титл", "Отправить титл", PlayerSendTitle.class, SendTitle.class, true),
    IF_PLAYER_NAME_EQUALS("Имя равно", "Имя равно", PlayerNameEquals.class, NameEquals.class, true);

    private String displayName;
    private String name;
    private Class<? extends Action> aClass;
    private Class<? extends CreativeSystemMenu> mClass;
    private boolean needChest;

    ActionType (String displayName, String name, Class<? extends Action> aClass, Class<? extends CreativeSystemMenu> mClass, boolean needChest) {
        this.displayName = displayName;
        this.name = name;
        this.aClass = aClass;
        this.mClass = mClass;
        this.needChest = needChest;
    }

    private static final Map<String, ActionType> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap((e) -> e.name().toUpperCase(Locale.ROOT), Function.identity()));

    public static @Nullable ActionType getByName(@NotNull String name) {
        return BY_NAME.get(name.toUpperCase(Locale.ROOT));
    }

    public static @Nullable ActionType getByDisplayName (@NotNull String displayName) {
        return BY_NAME.values().stream().filter((e) -> e.displayName.equalsIgnoreCase(displayName)).findFirst().orElse(null);
    }

    public static @Nullable ActionType getByCustomName (@NotNull String actionName) {
        return BY_NAME.values().stream().filter((e) -> e.name.equalsIgnoreCase(actionName)).findFirst().orElse(null);
    }

    public static @Nullable ActionType getByClass(@NotNull Class<? extends Action> aClass) {
        return BY_NAME.values().stream().filter((e) -> e.aClass.equals(aClass)).findFirst().orElse(null);
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getName() {
        return this.name;
    }

    public Class<? extends Action> getaClass() {
        return this.aClass;
    }

    public Class<? extends CreativeSystemMenu> getmClass() {
        return this.mClass;
    }

    public boolean getNeedChest() {
        return this.needChest;
    }


    public Action create (Starter starter, Inventory inventory) {
        Action a = null;

        try {
            Constructor<? extends Action> constructor = this.aClass.getConstructor(Starter.class,  String.class, Inventory.class);
            a = constructor.newInstance(starter, this.name, inventory);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return a;
    }

    public ActionIf createCondition (Starter starter, Inventory inventory, List<Action> condActions) {
        ActionIf a = null;

        try {
            Constructor<? extends ActionIf> constructor = (Constructor<? extends ActionIf>) this.aClass.getConstructor(Starter.class,  String.class, Inventory.class, List.class);
            a = constructor.newInstance(starter, this.name, inventory, condActions);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return a;
    }

}
