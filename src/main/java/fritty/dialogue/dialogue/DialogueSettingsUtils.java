package fritty.dialogue.dialogue;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class DialogueSettingsUtils {

    private DialogueSettingsUtils() {
    }

    public static Map<SettingKey, Object> merge(
            Map<SettingKey, Object> defaults,
            Map<SettingKey, Object> overrides
    ) {
        Map<SettingKey, Object> result =
                new EnumMap<>(SettingKey.class);

        if (defaults != null) {
            result.putAll(defaults);
        }

        if (overrides != null) {
            overrides.forEach((key, value) -> {
                if (key != null && value != null) {
                    SettingsValueValidator.validate(key, value);
                    result.put(key, value);
                }
            });
        }

        return result;
    }

    public static boolean hasOverride(
            DialogueSettings settings,
            SettingKey key
    ) {
        Objects.requireNonNull(settings, "Settings cannot be null");
        Objects.requireNonNull(key, "Setting key cannot be null");

        return settings.get(key) != null;
    }

    public static void reset(
            DialogueSettings settings,
            SettingKey key
    ) {
        Objects.requireNonNull(settings, "Settings cannot be null");
        Objects.requireNonNull(key, "Setting key cannot be null");

        settings.clear(key);
    }
}
