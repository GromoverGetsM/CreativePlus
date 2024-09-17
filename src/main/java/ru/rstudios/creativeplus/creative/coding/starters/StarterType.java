package ru.rstudios.creativeplus.creative.coding.starters;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.rstudios.creativeplus.creative.coding.actions.Action;
import ru.rstudios.creativeplus.creative.coding.starters.block.BlockDispenseStarter;
import ru.rstudios.creativeplus.creative.coding.starters.player.*;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum StarterType {

    PLAYER_JOIN("Игрок зашёл", "Вход", PlayerJoinStarter.class, PlayerJoinStarter.Event.class),
    PLAYER_QUIT("Игрок вышел", "Выход", PlayerQuitStarter.class, PlayerQuitStarter.Event.class),
    PLAYER_BLOCK_BREAK("Сломал блок", "Сломал блок", PlayerBreakBlockStarter.class, PlayerBreakBlockStarter.Event.class),
    PLAYER_BLOCK_PLACE("Поставил блок", "Поставил блок", PlayerPlaceBlockStarter.class, PlayerPlaceBlockStarter.Event.class),
    BLOCK_DISPENSE_ITEM("Блок выдал предмет", "Блок выдал предмет", BlockDispenseStarter.class, BlockDispenseStarter.Event.class),
    PLAYER_RIGHT_CLICKED("Правый клик", "Правый клик", PlayerRightClickStarter.class, PlayerRightClickStarter.Event.class);

    private String displayName;
    private String name;
    private Class<? extends Starter> sClass;
    private Class<? extends Event> eClass;

    StarterType (String displayName, String name, Class<? extends Starter> sClass, Class<? extends Event> eClass) {
        this.displayName = displayName;
        this.name = name;
        this.sClass = sClass;
        this.eClass = eClass;
    }

    private static final Map<String, StarterType> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap((e) -> e.name().toUpperCase(Locale.ROOT), Function.identity()));

    public static @Nullable StarterType getByName(@NotNull String name) {
        return BY_NAME.get(name.toUpperCase(Locale.ROOT));
    }

    public static @Nullable StarterType getByDisplayName (@NotNull String displayName) {
        return BY_NAME.values().stream().filter((e) -> e.displayName.equalsIgnoreCase(displayName)).findFirst().orElse(null);
    }

    public static @Nullable StarterType getByCustomName (@NotNull String starterName) {
        return BY_NAME.values().stream().filter((e) -> e.name.equalsIgnoreCase(starterName)).findFirst().orElse(null);
    }

    public static @Nullable StarterType getByClass(@NotNull Class<? extends Starter> sClass) {
        return BY_NAME.values().stream().filter((e) -> e.sClass.equals(sClass)).findFirst().orElse(null);
    }

    public String getDisplayName() {
        return this.displayName;
    }
    public String getName() {
        return this.name;
    }

    public Class<? extends Starter> getStarterClass() {
        return this.sClass;
    }

    public Class<? extends Event> getEventClass() {
        return this.eClass;
    }

    public Starter create (List<Action> actions) {
        Starter s = null;

        try {
            Constructor<? extends Starter> constructor = this.sClass.getConstructor(String.class, List.class);
            s = constructor.newInstance(this.name, actions);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return s;
    }
}
