package fritty.dialogue.dialogue;

import java.util.Objects;

public final class DialogueSettingsResolver {

    private DialogueSettingsResolver() {
    }

    public static Object resolve(
            Dialogue dialogue,
            Branch branch,
            SettingKey key
    ) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");
        Objects.requireNonNull(key, "Setting key cannot be null");

        if (branch != null) {
            Object branchValue = branch.getSettings().get(key);

            if (branchValue != null) {
                return branchValue;
            }
        }

        return dialogue.getSettings().get(key);
    }

    public static boolean resolveBoolean(
            Dialogue dialogue,
            Branch branch,
            SettingKey key,
            boolean defaultValue
    ) {
        Object value = resolve(dialogue, branch, key);

        return value instanceof Boolean booleanValue
                ? booleanValue
                : defaultValue;
    }

    public static double resolveNumber(
            Dialogue dialogue,
            Branch branch,
            SettingKey key,
            double defaultValue
    ) {
        Object value = resolve(dialogue, branch, key);

        return value instanceof Number number
                ? number.doubleValue()
                : defaultValue;
    }

    public static String resolveString(
            Dialogue dialogue,
            Branch branch,
            SettingKey key,
            String defaultValue
    ) {
        Object value = resolve(dialogue, branch, key);

        return value instanceof String string
                ? string
                : defaultValue;
    }
}
