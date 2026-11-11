package fritty.dialogue.dialogue;

import java.util.EnumMap;
import java.util.Map;

public class DialogueSettings {

    private final Map<SettingKey, Object> values =
            new EnumMap<>(SettingKey.class);

    public Object get(SettingKey key) {
        return values.get(key);
    }

    public void set(SettingKey key, Object value) {
        SettingsValueValidator.validate(key, value);
        values.put(key, value);
    }

    public void clear(SettingKey key) {
        if (key != null) {
            values.remove(key);
        }
    }

    public boolean has(SettingKey key) {
        return key != null && values.containsKey(key);
    }

    public Map<SettingKey, Object> getAll() {
        return new EnumMap<>(values);
    }

    public void copyFrom(DialogueSettings source) {
        if (source == null) {
            throw new IllegalArgumentException(
                    "Джерело налаштувань не може бути порожнім."
            );
        }

        values.clear();

        for (Map.Entry<SettingKey, Object> entry
                : source.values.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    public void clearAll() {
        values.clear();
    }

    public int size() {
        return values.size();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }
}
