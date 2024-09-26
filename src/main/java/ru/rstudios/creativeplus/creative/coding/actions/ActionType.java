package ru.rstudios.creativeplus.creative.coding.actions;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.rstudios.creativeplus.creative.coding.actions.actionvar.ActionSetVar;
import ru.rstudios.creativeplus.creative.coding.actions.gameaction.CancelEventAction;
import ru.rstudios.creativeplus.creative.coding.actions.ifplayer.PlayerMessageEndsWith;
import ru.rstudios.creativeplus.creative.coding.actions.ifplayer.PlayerMessageEquals;
import ru.rstudios.creativeplus.creative.coding.actions.ifplayer.PlayerMessageStartsWith;
import ru.rstudios.creativeplus.creative.coding.actions.ifplayer.PlayerNameEquals;
import ru.rstudios.creativeplus.creative.coding.actions.player.*;
import ru.rstudios.creativeplus.creative.coding.starters.Starter;
import ru.rstudios.creativeplus.creative.menus.CreativeSystemMenu;
import ru.rstudios.creativeplus.creative.menus.coding.actions.actionvar.VarSet;
import ru.rstudios.creativeplus.creative.menus.coding.actions.ifplayer.MessageEndsWith;
import ru.rstudios.creativeplus.creative.menus.coding.actions.ifplayer.MessageEquals;
import ru.rstudios.creativeplus.creative.menus.coding.actions.ifplayer.MessageStartsWith;
import ru.rstudios.creativeplus.creative.menus.coding.actions.ifplayer.NameEquals;
import ru.rstudios.creativeplus.creative.menus.coding.actions.playeraction.*;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ActionType {

    SEND_MESSAGE("Действие игрока", "Отправить сообщение", "Отправить сообщение", PlayerSendMessage.class, SendMessage.class, true),
    GIVE_ITEMS("Действие игрока", "Выдать предметы", "Выдать предметы", PlayerGiveItem.class, GiveItems.class, true),
    SEND_TITLE("Действие игрока", "Отправить титл", "Отправить титл", PlayerSendTitle.class, SendTitle.class, true),
    PLAY_SOUND("Действие игрока", "Проиграть звук", "Проиграть звук", PlayerPlaySound.class, PlaySound.class, true),
    SHOW_ADVANCEMENT("Действие игрока", "Отправить достижение", "Отправить достижение", PlayerSendAdvancementToast.class, SendAdvancementToast.class, true),
    IF_PLAYER_NAME_EQUALS("Если игрок", "Имя равно", "Имя равно", PlayerNameEquals.class, NameEquals.class, true),
    IF_PLAYER_MESSAGE_EQUALS("Если игрок", "Сообщение равно", "Сообщение равно", PlayerMessageEquals.class, MessageEquals.class, true),
    IF_PLAYER_MESSAGE_STARTS_WITH("Если игрок", "Сообщение начинается с", "Начинается с", PlayerMessageStartsWith.class, MessageStartsWith.class, true),
    IF_PLAYER_MESSAGE_ENDS_WITH("Если игрок", "Сообщение заканчивается на", "Заканчивается на", PlayerMessageEndsWith.class, MessageEndsWith.class, true),
    CANCEL_EVENT("Игровое действие", "Отменить событие", "Отменить событие", CancelEventAction.class, null, false),
    ACTION_VAR_SET("Работа с переменными", "Установить (=)", "Установить (=)", ActionSetVar.class, VarSet.class, true);


    private String rootBlock;
    private String displayName;
    private String name;
    private Class<? extends Action> aClass;
    private Class<? extends CreativeSystemMenu> mClass;
    private boolean needChest;

    ActionType (String rootBlock, String displayName, String name, Class<? extends Action> aClass, Class<? extends CreativeSystemMenu> mClass, boolean needChest) {
        this.rootBlock = rootBlock;
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

    public static @Nullable ActionType getByDisplayName (@NotNull String rootBlock, @NotNull String displayName) {
        return BY_NAME.values().stream().filter((e) -> e.displayName.equalsIgnoreCase(displayName) && e.rootBlock.equalsIgnoreCase(rootBlock)).findFirst().orElse(null);
    }

    public static @Nullable ActionType getByCustomName (@NotNull String rootBlock, @NotNull String actionName) {
        return BY_NAME.values().stream().filter((e) -> e.name.equalsIgnoreCase(actionName) && e.rootBlock.equalsIgnoreCase(rootBlock)).findFirst().orElse(null);
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
