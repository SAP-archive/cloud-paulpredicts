package com.sap.pto.util.configuration;

@SuppressWarnings("nls")
public class ConfigSectionParam {
    public enum Type {
        TEXT, PASSWORD, CHECKBOX, TEXTAREA
    }

    private String name;
    private String key;
    private String currentValue;
    private Type type = Type.TEXT;

    public ConfigSectionParam() {
    }

    public ConfigSectionParam(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public ConfigSectionParam(String key, String name, Type type) {
        this(key, name);

        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ConfigSectionParam [name=" + name + ", key=" + key + ", currentValue=" + currentValue + ", type=" + type + "]";
    }

}
