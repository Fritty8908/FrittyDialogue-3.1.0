package fritty.dialogue.dialogue;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class DialogueSettingsInheritanceResolver {

    private DialogueSettingsInheritanceResolver() {
    }

    public static Map<SettingKey, Object> resolve(
            Dialogue dialogue,
            Branch branch
    ) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");

        Map<SettingKey, Object> result =
                new EnumMap<>(SettingKey.class);

        result.putAll(DialogueSettingsDefaults.create());

        merge(result, dialogue.getSettings());

        if (branch != null) {
            merge(result, branch.getSettings());
        }

        return result;
    }

    private static void merge(
            Map<SettingKey, Object> target,
            DialogueSettings source
    ) {
        if (source == null) {
            return;
        }

        source.getAll().forEach((key, value) -> {
            if (key != null && value != null) {
                SettingsValueValidator.validate(key, value);
                target.put(key, value);
            }
        });
    }
}
