package ru.rstudios.creativeplus.creative.coding.dynamicvariables;

import ru.rstudios.creativeplus.creative.plots.Plot;

public class DynamicVariable {

    private String name;
    private Object value;
    private boolean isSaved;

    public DynamicVariable (String name) {
        this(name, null);
    }

    public DynamicVariable (String name, Object value) {
        this(name, value, false);
    }

    public DynamicVariable (String name, Object value, boolean isSaved) {
        this.name = name;
        this.value = value;
        this.isSaved = isSaved;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }

    public boolean isSaved() {
        return this.isSaved;
    }

    public void setName (String name) {
        this.name = cutManySymbols(name);
    }

    public void setValue (Plot plot, Object value, boolean isSaved) {
        if (value instanceof String) {
            value = cutManySymbols(value.toString());
        }

        this.value = value;
        this.isSaved = isSaved;
    }

    public static String cutManySymbols (String s) {
        return s.length() > 1024 ? s.substring(0, 1024) : s;
    }
}
