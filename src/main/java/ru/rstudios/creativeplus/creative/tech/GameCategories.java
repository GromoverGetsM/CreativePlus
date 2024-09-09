package ru.rstudios.creativeplus.creative.tech;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum GameCategories {

    SANDBOX("Песочница", "Стартовая категория для мира игрока"),
    EXPERIMENTAL("Эксперимент", "Экспериментальный мир"),
    TEST("Тест", "Мир для тестов кода и обновлений");

    private String name;
    private String desc;

    private static final Map<String, GameCategories> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap((e) -> e.name().toUpperCase(Locale.ROOT), Function.identity()));

    GameCategories(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public static @Nullable GameCategories getByName (String name) {
        return BY_NAME.get(name.toUpperCase());
    }

    public static @NotNull GameCategories getByCustomName (String name) {
        return BY_NAME.values().stream().filter((e) -> e.name.equalsIgnoreCase(name)).findFirst().orElse(GameCategories.SANDBOX);
    }

    public static @NotNull GameCategories getByDesc (String desc) {
        return BY_NAME.values().stream().filter((e) -> e.desc.equalsIgnoreCase(desc)).findFirst().orElse(GameCategories.SANDBOX);
    }

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.desc;
    }

}
