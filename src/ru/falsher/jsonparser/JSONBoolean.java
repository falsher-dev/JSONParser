package ru.falsher.jsonparser;

public class JSONBoolean extends JSONElement {

    public static final JSONBoolean
            TRUE  = new JSONBoolean(true),
            FALSE = new JSONBoolean(false);

    public final boolean value;

    public JSONBoolean(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
