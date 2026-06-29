package fr.madu59.obe.client.config;

import net.minecraft.client.resources.language.I18n;

public class Option<T> {
    private String id;
    private String name;
    private String description;
    private T value;
    private T defaultValue;
    private Runnable action;

    public Option(String id, String name, String description, T value, T defaultValue, Runnable action) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.value = value;
        this.defaultValue = defaultValue;
        this.action = action;
        SettingsManager.ALL_OPTIONS.put(id, this);
    }

    public void resetToDefault() {
        this.value = this.defaultValue;
    }

    public Runnable getRunnable() {
        return this.action;
    }

    public T getValue() {
        return this.value;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return I18n.get(this.name);
    }

    public String getDescription() {
        return I18n.get(this.description);
    }

    public void setToNextValue() {
        this.value = cycle(this.value);
    }

    public void setValue(T value){
        this.value = value;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    @SuppressWarnings("unchecked")
    public T cycle(T value) {
        if (value instanceof Enum<?> enumValue) {
            Enum<?>[] constants = enumValue.getDeclaringClass().getEnumConstants();
            int nextOrdinal = (enumValue.ordinal() + 1) % constants.length;
            return (T) constants[nextOrdinal];
        }
        if(value instanceof Boolean boolValue) {
            return (T) Boolean.valueOf(!boolValue);
        }
        else return null;
    }
}
