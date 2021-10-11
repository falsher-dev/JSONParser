package ru.falsher.jsonparser;

import java.util.*;
import java.util.function.Consumer;

public class JSONObject extends JSONElement {

    private String[] keys;
    private JSONElement[] values;
    private int len;

    private transient KeySet keySet = null;
    private transient Collection<JSONElement> valuesCollection = null;

    public JSONObject() {
        this.keys = new String[8];
        this.values = new JSONElement[8];
    }

    public void append(String key, JSONElement value){
        if (len == keys.length) {
            keys = Arrays.copyOf(keys, len + 8);
            values = Arrays.copyOf(values, len + 8);
        }
        keys[len] = key;
        values[len++] = value;
    }



    public JSONObject reset(String key, JSONElement value){
        boolean has_letter = false;
        for (int i = 0; i < len; i++) if (keys[i].equals(key)) {
            values[i] = value;
            has_letter = true;
        }
        if (!has_letter) append(key,value);
        return this;
    }

    public JSONElement remove(String s){
        for (int i = 0; i < len; i++) if (keys[i].equals(s)) {
            JSONElement ret = values[i];

            i++;

            while (i < len) {
                keys[i-1] = keys[i];
                values[i-1] = values[i++];
            }

            keys[i] = null;
            values[i] = null;

            len--;
            return ret;
        }
        return null;
    }

    final class KeyIterator implements Iterator<String> {
        int i = 0;

        public final String next() {
            return hasNext()
                    ? keys[i++]
                    : null;
        }

        @Override
        public final boolean hasNext(){
            return i < len;
        }
    }

    final class ValueIterator implements Iterator<JSONElement> {
        int i = 0;

        public final JSONElement next() {
            return hasNext()
                    ? values[i++]
                    : null;
        }

        @Override
        public final boolean hasNext(){
            return i < len;
        }
    }



    public KeySet keySet(){
        KeySet ks = keySet;
        if (ks == null) {
            ks = new KeySet();
            keySet = ks;
        }
        return ks;
    }

    public Collection<JSONElement> values(){
        Collection<JSONElement> vs = valuesCollection;
        if (vs == null) {
            vs = new valuesCollection();
            valuesCollection = vs;
        }
        return vs;
    }

    public final class KeySet extends AbstractSet<String> {
        private int i = 0;

        public final int size()                 { return len; }
        public final void clear()               { JSONObject.this.clear(); }
        public final Iterator<String> iterator()     { return new KeyIterator(); }
        public final boolean contains(Object o) { return o instanceof String && containsKey((String) o); }
        public final boolean remove(Object key) {
            return key instanceof String && JSONObject.this.remove((String) key) != null;
        }
        public final void forEach(Consumer<? super String> action) {
            i=0;
            if (action == null)
                throw new NullPointerException();
            while (i<len) {
                action.accept(keys[i]);
                i++;
            }
        }

        public String key(){
            return keys[i];
        }

        public JSONElement value(){
            return values[i];
        }
    }

    final class valuesCollection extends AbstractCollection<JSONElement> {
        public final int size()                 { return len; }
        public final void clear()               { JSONObject.this.clear(); }
        public final Iterator<JSONElement> iterator()     { return new ValueIterator(); }
        public final boolean contains(Object o) { return o instanceof JSONElement && containsValue((JSONElement) o); }
        public final void forEach(Consumer<? super JSONElement> action) {
            if (action == null)
                throw new NullPointerException();
            for (JSONElement el: values) {
                action.accept(el);
            }
        }
    }

    public boolean containsKey(String s){
        for (String key: keys) if (key.equals(s)) return true;
        return false;
    }

    public boolean containsValue(JSONElement el){
        for (JSONElement value: values) if (value.equals(el)) return true;
        return false;
    }

    public void clear(){
        keys = new String[8];
        values = new JSONElement[8];
        len = 0;
    }

    public JSONElement get(String key){
        for (int i = 0; i < len; i++) if (keys[i].equals(key)) return values[i];
        return null;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.appendCodePoint('{');
        for (int i=0;i<len;i++){
            ret.appendCodePoint('\n');
            ret.appendCodePoint('\t');
            ret.appendCodePoint('"');
            ret.append(keys[i]);
            ret.appendCodePoint('"');
            ret.appendCodePoint(':');
            String[] tmp = values[i].toString().split("\n");
            for (int o=0; o<tmp.length; o++) {
                ret.appendCodePoint('\t');
                ret.append(tmp[o]);
                if (o != tmp.length - 1) ret.appendCodePoint('\n');
            }
            if (i != len-1) ret.appendCodePoint(',');
        }
        if (len != 0) ret.append("\n");
        ret.appendCodePoint('}');
        return ret.toString();
    }

}
