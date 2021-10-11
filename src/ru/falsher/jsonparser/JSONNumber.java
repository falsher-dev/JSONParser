package ru.falsher.jsonparser;

public class JSONNumber extends JSONElement {
    private final Number value;

    public JSONNumber(Number value) {
        this.value = value;
    }

    public int intValue() {
        return value.intValue();
    }

    public long longValue() {
        return value.longValue();
    }

    public float floatValue() {
        return value.floatValue();
    }

    public double doubleValue() {
        return value.doubleValue();
    }

    @Override
    public String toString() {
        return Double.toString(doubleValue());
    }
}
