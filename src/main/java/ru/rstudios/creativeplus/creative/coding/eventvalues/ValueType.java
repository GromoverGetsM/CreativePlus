package ru.rstudios.creativeplus.creative.coding.eventvalues;

import org.jetbrains.annotations.Nullable;
import ru.rstudios.creativeplus.creative.coding.eventvalues.values.EventMessage;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ValueType {

    PLAYER_MESSAGE("Сообщение игрока", EventMessage.class);

    private String message;
    private Class<? extends Value> clazz;

    ValueType (String message, Class<? extends Value> clazz) {
        this.clazz = clazz;
    }

    private static final Map<String, ValueType> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap((e) -> e.name().toUpperCase(Locale.ROOT), Function.identity()));

    public String getMessage() {
        return this.message;
    }

    public Class<? extends Value> getClazz() {
        return this.clazz;
    }

    public static @Nullable ValueType getByName (String name) {
        return BY_NAME.get(name.toUpperCase());
    }

    public static @Nullable ValueType getByMessage (String message) {
        return BY_NAME.values().stream().filter((e) -> e.message.equalsIgnoreCase(message)).findFirst().orElse(null);
    }

    public static @Nullable ValueType getByClazz (Class<? extends Value> clazz) {
        return BY_NAME.values().stream().filter((e) -> e.clazz.equals(clazz)).findFirst().orElse(null);
    }

}
