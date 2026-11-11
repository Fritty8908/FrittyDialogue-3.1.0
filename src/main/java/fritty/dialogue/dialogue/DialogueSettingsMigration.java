package fritty.dialogue.dialogue;

import java.util.LinkedHashMap;
import java.util.Map;

public final class DialogueSettingsMigration {

    private DialogueSettingsMigration() {
    }

    public static Map<SettingKey, Object> migrate(
            Map<String, Object> legacySettings
    ) {
        Map<SettingKey, Object> migrated = new LinkedHashMap<>(
                DialogueSettingsDefaults.create()
        );

        if (legacySettings == null || legacySettings.isEmpty()) {
            return migrated;
        }

        for (Map.Entry<String, Object> entry : legacySettings.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }

            SettingKey key;

            try {
                key = SettingKey.valueOf(
                        entry.getKey().trim().toUpperCase()
                                .replace('-', '_')
                );
            } catch (IllegalArgumentException exception) {
                continue;
            }

            Object value = entry.getValue();

            if (value == null) {
                continue;
            }

            try {
                SettingsValueValidator.validate(key, value);
                migrated.put(key, value);
            } catch (IllegalArgumentException ignored) {
                // Некоректне старе значення замінюється значенням за замовчуванням.
            }
        }

        return migrated;
    }
}
