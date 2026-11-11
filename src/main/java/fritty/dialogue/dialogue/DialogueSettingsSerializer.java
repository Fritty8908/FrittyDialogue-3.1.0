package fritty.dialogue.dialogue;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class DialogueSettingsSerializer {

    private DialogueSettingsSerializer() {
    }

    public static Map<String, Object> serialize(DialogueSettings settings) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (settings == null) {
            return result;
        }

        for (Map.Entry<SettingKey, Object> entry
                : settings.getAll().entrySet()) {
            if (entry.getValue() != null) {
                result.put(
                        entry.getKey().name().toLowerCase(),
                        entry.getValue()
                );
            }
        }

        return result;
    }

    public static DialogueSettings deserialize(
            Map<String, Object> serialized
    ) {
        DialogueSettings settings = new DialogueSettings();

        if (serialized == null || serialized.isEmpty()) {
            return settings;
        }

        Map<SettingKey, Object> migrated =
                DialogueSettingsMigration.migrate(serialized);

        for (Map.Entry<SettingKey, Object> entry
                : migrated.entrySet()) {
            try {
                SettingsValueValidator.validate(
                        entry.getKey(),
                        entry.getValue()
                );
                settings.set(entry.getKey(), entry.getValue());
            } catch (IllegalArgumentException ignored) {
                // Некоректні значення пропускаються.
            }
        }

        return settings;
    }
}
