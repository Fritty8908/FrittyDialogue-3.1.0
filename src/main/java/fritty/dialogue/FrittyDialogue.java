
package fritty.dialogue.dialogue;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Stores explicitly configured values for a dialogue or branch.
 *
 * Dialogue settings contain dialogue-level overrides.
 * Branch settings contain branch-level overrides.
 *
 * Inherited and default values are resolved separately by
 * DialogueSettingsInheritanceResolver.
 */
public class DialogueSettings {

    private final Map<SettingKey, Object> values =
            new EnumMap<>(SettingKey.class);

    /**
     * Returns an explicitly stored value.
     * Does not resolve defaults or inherited values.
     */
    public Object get(SettingKey key) {
        if (key == null) {
            return null;
        }

        return values.get(key);
    }

    /**
     * Returns an explicitly stored value using its config key.
     */
    public Object get(String key) {
        SettingKey settingKey = resolveKey(key);

        return settingKey == null
                ? null
                : get(settingKey);
    }

    /**
     * Stores a value using its enum key.
     */
    public void set(SettingKey key, Object value) {
        Objects.requireNonNull(
                key,
                "Setting key cannot be null"
        );

        SettingsValueValidator.validate(key, value);
        values.put(key, value);
    }

    /**
     * Stores a value using its serialized config key.
     */
    public void set(String key, Object value) {
        SettingKey settingKey = requireKey(key);
        set(settingKey, value);
    }

    /**
     * Checks whether this object explicitly overrides a setting.
     */
    public boolean has(SettingKey key) {
        return key != null && values.containsKey(key);
    }

    /**
     * Checks whether this object explicitly overrides a setting.
     */
    public boolean has(String key) {
        SettingKey settingKey = resolveKey(key);

        return settingKey != null && has(settingKey);
    }

    /**
     * Compatibility alias used by existing branch code.
     */
    public boolean contains(SettingKey key) {
        return has(key);
    }

    /**
     * Compatibility alias used by existing branch code.
     */
    public boolean contains(String key) {
        return has(key);
    }

    /**
     * Removes an explicitly stored setting.
     * The next resolution will use the inherited or default value.
     */
    public void clear(SettingKey key) {
        if (key != null) {
            values.remove(key);
        }
    }

    /**
     * Removes an explicitly stored setting by config key.
     */
    public void clear(String key) {
        SettingKey settingKey = resolveKey(key);

        if (settingKey != null) {
            clear(settingKey);
        }
    }

    /**
     * Compatibility method used by existing branch code.
     *
     * @return true if a value was removed
     */
    public boolean remove(String key) {
        SettingKey settingKey = resolveKey(key);

        if (settingKey == null) {
            return false;
        }

        return values.remove(settingKey) != null;
    }

    /**
     * Returns a defensive copy of all explicit overrides.
     */
    public Map<SettingKey, Object> getAll() {
        return new EnumMap<>(values);
    }

    /**
     * Replaces all explicit overrides with those from another object.
     */
    public void copyFrom(DialogueSettings source) {
        Objects.requireNonNull(
                source,
                "Source settings cannot be null"
        );

        if (source == this) {
            return;
        }

        values.clear();

        for (Map.Entry<SettingKey, Object> entry
                : source.values.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Copies explicit overrides from a map.
     */
    public void putAll(Map<SettingKey, Object> source) {
        if (source == null || source.isEmpty()) {
            return;
        }

        for (Map.Entry<SettingKey, Object> entry
                : source.entrySet()) {
            if (entry.getKey() != null
                    && entry.getValue() != null) {
                set(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Removes every explicit override.
     */
    public void clearAll() {
        values.clear();
    }

    public int size() {
        return values.size();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    /**
     * Resolves a serialized key without throwing.
     */
    private SettingKey resolveKey(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }

        return SettingKey.fromKey(key);
    }

    /**
     * Resolves a serialized key and rejects unknown settings.
     */
    private SettingKey requireKey(String key) {
        SettingKey settingKey = resolveKey(key);

        if (settingKey == null) {
            throw new IllegalArgumentException(
                    "Невідомий параметр налаштувань: " + key
            );
        }

        return settingKey;
    }
}
