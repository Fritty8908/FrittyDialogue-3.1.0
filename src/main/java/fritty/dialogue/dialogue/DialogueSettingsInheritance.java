package fritty.dialogue.dialogue;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class DialogueSettingsInheritance {

    private DialogueSettingsInheritance() {
    }

    public static Map<SettingKey, Object> resolveAll(
            Dialogue dialogue,
            Branch branch
    ) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");

        Map<SettingKey, Object> resolved =
                new EnumMap<>(SettingKey.class);

        resolved.putAll(DialogueSettingsDefaults.create());

        if (dialogue.getSettings() != null) {
            for (SettingKey key : SettingKey.values()) {
                Object value = dialogue.getSettings().get(key);

                if (value != null) {
                    resolved.put(key, value);
                }
            }
        }

        if (branch != null && branch.getSettings() != null) {
            for (SettingKey key : SettingKey.values()) {
                Object value = branch.getSettings().get(key);

                if (value != null) {
                    resolved.put(key, value);
                }
            }
        }

        return resolved;
    }
}
