package fritty.dialogue.dialogue;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class DialogueSettingsCopy {

    private DialogueSettingsCopy() {
    }

    public static DialogueSettings copy(DialogueSettings source) {
        Objects.requireNonNull(source, "Settings cannot be null");

        DialogueSettings copy = new DialogueSettings();

        for (SettingKey key : SettingKey.values()) {
            Object value = source.get(key);

            if (value != null) {
                copy.set(key, value);
            }
        }

        return copy;
    }

    public static Map<SettingKey, Object> copyValues(
            Map<SettingKey, Object> source
    ) {
        Objects.requireNonNull(source, "Settings map cannot be null");

        Map<SettingKey, Object> copy =
                new EnumMap<>(SettingKey.class);

        copy.putAll(source);
        return copy;
    }
}
