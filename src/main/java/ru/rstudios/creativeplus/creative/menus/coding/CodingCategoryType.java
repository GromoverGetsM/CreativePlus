package ru.rstudios.creativeplus.creative.menus.coding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.rstudios.creativeplus.creative.menus.coding.actions.ActionVar;
import ru.rstudios.creativeplus.creative.menus.coding.actions.GameAction;
import ru.rstudios.creativeplus.creative.menus.coding.actions.PlayerAction;
import ru.rstudios.creativeplus.creative.menus.coding.actions.ifPlayer;
import ru.rstudios.creativeplus.creative.menus.coding.starters.BlockEvent;
import ru.rstudios.creativeplus.creative.menus.coding.starters.PlayerEvent;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CodingCategoryType {

    PLAYER_EVENT_WORLD_INTERACT_CATEGORY("Взаимодействие с миром", PlayerEvent.WorldInteractCategory.class, PlayerEvent.class),
    PLAYER_EVENT_OTHER_CATEGORY("Другое", PlayerEvent.OtherCategory.class, PlayerEvent.class),
    BLOCK_EVENT_MACHINE_CATEGORY("Механизмы", BlockEvent.MachineCategory.class, BlockEvent.class),
    PLAYER_ACTION_COMMUNICATION_CATEGORY("Коммуникация", PlayerAction.CommunicationCategory.class, PlayerAction.class),
    PLAYER_ACTION_INVENTORY_CATEGORY("Управление инвентарём", PlayerAction.InventoryCategory.class, PlayerAction.class),
    IF_PLAYER_TEXT_CONDS_CATEGORY("Текстовые условия", ifPlayer.TextConditionsCategory.class, ifPlayer.class),
    GAME_ACTION_CODE_UTIL_CATEGORY("Утилиты кода", GameAction.CodeUtilCategory.class, GameAction.class),
    ACTION_VAR_ASSIGNMENT_CATEGORY("Присвоение", ActionVar.AssignmentCategory.class, ActionVar.class);


    public String categoryDisplayName;
    public Class<? extends AbstractCategoryMenu> categoryClass;
    public Class<? extends AbstractSelectCategoryMenu> mainSuperClass;

    CodingCategoryType (String categoryDisplayName, Class<? extends AbstractCategoryMenu> categoryClass, Class<? extends AbstractSelectCategoryMenu> mainSuperClass) {
        this.categoryDisplayName = categoryDisplayName;
        this.categoryClass = categoryClass;
        this.mainSuperClass = mainSuperClass;
    }

    private static final Map<String, CodingCategoryType> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap((e) -> e.name().toUpperCase(Locale.ROOT), Function.identity()));

    public static @Nullable CodingCategoryType getByDisplayName (@NotNull String categoryDisplayName) {
        return BY_NAME.values().stream().filter((e) -> e.categoryDisplayName.equalsIgnoreCase(categoryDisplayName)).findFirst().orElse(null);
    }

}
