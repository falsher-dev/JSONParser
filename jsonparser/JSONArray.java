package ru.falsher.jsonparser;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

public class JSONArray extends JSONElement implements Iterable<JSONElement> {

    private JSONElement[] values;
    private int len=0;

    public JSONArray() {
        this.values = new JSONElement[8];
    }

    @Override
    public Iterator<JSONElement> iterator() {
        return new Iter();
    }

    @Override
    public void forEach(Consumer<? super JSONElement> action) {
        Objects.requireNonNull(action);
        for (int i=0; i<len; i++) {
            action.accept(values[i]);
        }
    }

    public JSONArray append(JSONElement el){
        if (len == values.length) values = Arrays.copyOf(values, len + 8);
        values[len++] = el;
        return this;
    }

    public JSONElement get(int i){
        return i < len ? values[i] : null;
    }

    public int length(){
        return len;
    }

    private class Iter implements Iterator<JSONElement>{
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < len;
        }

        @Override
        public JSONElement next() {
            return index < len ? values[index++] : null;
        }
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.appendCodePoint('[');
        for (int i=0;i<len;i++){
            String[] tmp = values[i].toString().split("\n");
            for (String line: tmp) {
                ret.appendCodePoint('\n');
                ret.appendCodePoint('\t');
                ret.append(line);
            }
            if (i != len-1) ret.appendCodePoint(',');
        }
        if (len != 0) ret.appendCodePoint('\n');
        ret.appendCodePoint(']');
        return ret.toString();
    }
}
