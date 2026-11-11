package fritty.dialogue.dialogue;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class DialogueSettings {

    private final Map<String, Object> values = new LinkedHashMap<>();

    public void set(String key, Object value) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException(
                    "Setting key cannot be empty"
            );
        }

        if (value == null) {
            values.remove(key);
            return;
        }

        SettingKey settingKey = SettingKey.fromKey(key);

        if (settingKey == null) {
            validateGenericValue(value);
            values.put(key, value);
            return;
        }

        Object validatedValue = validateValue(settingKey, value);
        values.put(key, validatedValue);
    }

    public void set(SettingKey key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException(
                    "Setting key cannot be null"
            );
        }

        set(key.getKey(), value);
    }

    private Object validateValue(
            SettingKey key,
            Object value
    ) {
        return switch (key.getType()) {

            case STRING -> {
                if (!(value instanceof String)) {
                    throw invalidType(key, "String");
                }

                yield value;
            }

            case BOOLEAN -> {
                if (!(value instanceof Boolean)) {
                    throw invalidType(key, "Boolean");
                }

                yield value;
            }

            case INTEGER -> {
                if (!(value instanceof Number number)) {
                    throw invalidType(key, "Integer");
                }

                double numericValue = number.doubleValue();

                if (!Double.isFinite(numericValue)
                        || numericValue != Math.rint(numericValue)
                        || numericValue < Integer.MIN_VALUE
                        || numericValue > Integer.MAX_VALUE) {
                    throw new IllegalArgumentException(
                            "Setting '" + key.getKey()
                                    + "' requires a valid integer"
                    );
                }

                yield (int) numericValue;
            }

            case DOUBLE -> {
                if (!(value instanceof Number number)) {
                    throw invalidType(key, "Double");
                }

                double numericValue = number.doubleValue();

                if (!Double.isFinite(numericValue)) {
                    throw new IllegalArgumentException(
                            "Setting '" + key.getKey()
                                    + "' requires a finite number"
                    );
                }

                yield numericValue;
            }
        };
    }

    private IllegalArgumentException invalidType(
            SettingKey key,
            String expectedType
    ) {
        return new IllegalArgumentException(
                "Setting '" + key.getKey()
                        + "' requires type " + expectedType
        );
    }

    private void validateGenericValue(Object value) {
        if (!(value instanceof String
                || value instanceof Boolean
                || value instanceof Integer
                || value instanceof Long
                || value instanceof Double
                || value instanceof Float)) {

            throw new IllegalArgumentException(
                    "Unsupported setting value type: "
                            + value.getClass().getName()
            );
        }
    }

    public Object get(String key) {
        return values.get(key);
    }

    public Object get(SettingKey key) {
        return key == null ? null : get(key.getKey());
    }

    public Object get(String key, Object defaultValue) {
        return values.getOrDefault(key, defaultValue);
    }

    public String getString(
            String key,
            String defaultValue
    ) {
        Object value = values.get(key);

        return value instanceof String string
                ? string
                : defaultValue;
    }

    public boolean getBoolean(
            String key,
            boolean defaultValue
    ) {
        Object value = values.get(key);

        return value instanceof Boolean bool
                ? bool
                : defaultValue;
    }

    public int getInt(
            String key,
            int defaultValue
    ) {
        Object value = values.get(key);

        return value instanceof Number number
                ? number.intValue()
                : defaultValue;
    }

    public double getDouble(
            String key,
            double defaultValue
    ) {
        Object value = values.get(key);

        return value instanceof Number number
                ? number.doubleValue()
                : defaultValue;
    }

    public boolean contains(String key) {
        return values.containsKey(key);
    }

    public boolean contains(SettingKey key) {
        return key != null && contains(key.getKey());
    }

    public void remove(String key) {
        values.remove(key);
    }

    public void remove(SettingKey key) {
        if (key != null) {
            remove(key.getKey());
        }
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

        values.clear();
        values.putAll(other.values);
    }

    public DialogueSettings copy() {
        DialogueSettings copy = new DialogueSettings();
        copy.values.putAll(values);
        return copy;
    }
}
