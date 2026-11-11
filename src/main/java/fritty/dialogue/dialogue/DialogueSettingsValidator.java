package fritty.dialogue.dialogue;

import java.util.Objects;

public final class DialogueSettingsValidator {

    private DialogueSettingsValidator() {
    }

    public static void validate(DialogueSettings settings) {
        Objects.requireNonNull(settings, "Settings cannot be null");

        for (SettingKey key : SettingKey.values()) {
            Object value = settings.get(key);

            if (value != null) {
                SettingsValueValidator.validate(key, value);
            }
        }
    }

    public static void validate(
            Dialogue dialogue,
            Branch branch
    ) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");

        validate(dialogue.getSettings());

        if (branch != null) {
            validate(branch.getSettings());
        }
    }
}
