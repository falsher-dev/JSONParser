package ru.falsher.jsonparser;

public class JSONNull extends JSONElement {
    public static final JSONNull INSTANCE = new JSONNull();

    @Override
    public String toString() {
        return "null";
    }
}
