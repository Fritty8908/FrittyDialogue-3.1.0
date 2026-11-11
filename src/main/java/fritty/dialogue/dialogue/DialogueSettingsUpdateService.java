package fritty.dialogue.dialogue;

import java.util.Objects;

public final class DialogueSettingsUpdateService {

    public void set(
            Dialogue dialogue,
            SettingKey key,
            Object value
    ) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");
        Objects.requireNonNull(key, "Setting key cannot be null");

        validateValue(key, value);
        dialogue.getSettings().set(key, value);
    }

    public void clear(Dialogue dialogue, SettingKey key) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");
        Objects.requireNonNull(key, "Setting key cannot be null");

        dialogue.getSettings().clear(key);
    }

    private void validateValue(SettingKey key, Object value) {
        Objects.requireNonNull(value, "Setting value cannot be null");

        Class<?> expectedType = switch (key) {
            case HIDE_PLAYERS,
                 ONE_TIME,
                 SEE_THROUGH_BLOCKS,
                 SMOOTH_TELEPORT,
                 USE_SLOWNESS,
                 MAKE_INVISIBLE,
                 SAVE_START_LOCATION,
                 ACTIONBAR_HINTS -> Boolean.class;

            case CHAR_DELAY,
                 BACKGROUND_ALPHA,
                 LINE_WIDTH,
                 TEXT_SCALE,
                 CHAR_PITCH,
                 SOUND_VOLUME,
                 PER_CHAR -> Number.class;

            default -> String.class;
        };

        if (!expectedType.isInstance(value)) {
            throw new IllegalArgumentException(
                    "Неправильний тип значення для налаштування "
                            + key.name()
                            + ". Очікується: "
                            + expectedType.getSimpleName()
            );
        }
    }
}
