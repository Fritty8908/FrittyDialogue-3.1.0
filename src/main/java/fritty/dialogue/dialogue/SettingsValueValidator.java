package fritty.dialogue.dialogue;

import java.util.Objects;

public final class SettingsValueValidator {

    private SettingsValueValidator() {
    }

    public static void validate(SettingKey key, Object value) {
        Objects.requireNonNull(key, "Setting key cannot be null");
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
                    "Неправильний тип значення для "
                            + key.name()
                            + ". Очікується: "
                            + expectedType.getSimpleName()
            );
        }

        if (value instanceof Number number) {
            validateNumber(key, number.doubleValue());
        }

        if (value instanceof String string && string.length() > 4096) {
            throw new IllegalArgumentException(
                    "Значення налаштування надто довге: " + key.name()
            );
        }
    }

    private static void validateNumber(SettingKey key, double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(
                    "Числове значення має бути скінченним."
            );
        }

        switch (key) {
            case CHAR_DELAY -> requireRange(key, value, 0, 1000);
            case BACKGROUND_ALPHA -> requireRange(key, value, 0, 255);
            case LINE_WIDTH -> requireRange(key, value, 1, 1000);
            case TEXT_SCALE -> requireRange(key, value, 0.1, 10);
            case CHAR_PITCH -> requireRange(key, value, 0, 2);
            case SOUND_VOLUME -> requireRange(key, value, 0, 1);
            case PER_CHAR -> requireRange(key, value, 0, 100);
            default -> {
            }
        }
    }

    private static void requireRange(
            SettingKey key,
            double value,
            double minimum,
            double maximum
    ) {
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(
                    "Значення " + key.name()
                            + " має бути в діапазоні "
                            + minimum + "–" + maximum + "."
            );
        }
    }
}
