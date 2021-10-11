package ru.falsher.jsonparser;

public class JSONString extends JSONElement {

    public final String value;

    public JSONString(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return '\"' + value + '\"';
    }
}
