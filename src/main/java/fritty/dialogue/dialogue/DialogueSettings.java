package fritty.dialogue.dialogue;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class DialogueSettings {

    private final Map<String, Object> values = new LinkedHashMap<>();

    public void set(String key, Object value) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Setting key cannot be empty");
        }

        if (value == null) {
            values.remove(key);
            return;
        }

        if (!(value instanceof String
                || value instanceof Boolean
                || value instanceof Integer
                || value instanceof Long
                || value instanceof Double
                || value instanceof Float)) {
            throw new IllegalArgumentException(
                    "Unsupported setting value type: " + value.getClass().getName()
            );
        }

        values.put(key, value);
    }

    public Object get(String key) {
        return values.get(key);
    }

    public Object get(String key, Object defaultValue) {
        return values.getOrDefault(key, defaultValue);
    }

    public String getString(String key, String defaultValue) {
        Object value = values.get(key);
        return value instanceof String string ? string : defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = values.get(key);
        return value instanceof Boolean bool ? bool : defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        Object value = values.get(key);

        if (value instanceof Number number) {
            return number.intValue();
        }

        return defaultValue;
    }

    public double getDouble(String key, double defaultValue) {
        Object value = values.get(key);

        if (value instanceof Number number) {
            return number.doubleValue();
        }

        return defaultValue;
    }

    public boolean contains(String key) {
        return values.containsKey(key);
    }

    public void remove(String key) {
        values.remove(key);
    }

    public void clear() {
        values.clear();
    }

    public Map<String, Object> getAll() {
        return Collections.unmodifiableMap(values);
    }

    public void copyFrom(DialogueSettings other) {
        if (other == null) {
            return;
        }

        values.putAll(other.values);
    }

    public DialogueSettings copy() {
        DialogueSettings copy = new DialogueSettings();
        copy.values.putAll(values);
        return copy;
    }
}
